package org.book.commerce.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.productservice.config.DistributedLock;
import org.book.commerce.productservice.dto.*;
import org.book.commerce.productservice.repository.ImageRepository;
import org.book.commerce.productservice.repository.ProductRepository;
import org.book.commerce.productservice.domain.Image;
import org.book.commerce.productservice.domain.Product;
import org.book.commerce.productservice.service.mapper.ImageMapper;
import org.book.commerce.productservice.service.mapper.ProductMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ImageUploadService imageUploadService;

    @Transactional
    public ResponseAddProduct addProduct(AddProductDto addProductDto) {
        Product product = Product.builder().stock(addProductDto.getStock()).name(addProductDto.getName()).price(addProductDto.getPrice())
                .description(addProductDto.getDescription())
                .thumbnailName(addProductDto.getImageName()).thumbnailUrl(addProductDto.getImageUrl()).isLimitedEdition(addProductDto.getIsLimitedEdition())
                .openDateTime(addProductDto.getOpenDateTime()).build();
        Long productId = productRepository.save(product).getProductId();
        imageUploadService.upload(productId, addProductDto.getImageName(), addProductDto.getImageUrl());
        return ResponseAddProduct.builder().productId(productId).message("상품 추가가 완료되었습니다!").build();
        }

    @Transactional
    public ResponseEntity<List<AllProductList>> getProducts() {
        List<Product> productlist = productRepository.findAll();
        List<AllProductList> productDtoList = productlist.stream().map(ProductMapper.INSTANCE::ProductEntityToDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(productDtoList);
    }

    /**
     *  <getProductDetail>
     *  1. 만약 한정판 제품이라면(isLimitedEdition==true), isOpen을 false로 반환(오픈시간이 되지 않았을 경우), 오픈시간도 제공
     *  2. 한정판 제품이 아니라면, 기본적으로 isOpen는 true로 설정
     */
    @Transactional
    public ResponseEntity<ProductDetail> getProductDetail(Long productId) {
        Product product = findProductById(productId);
        List<Image> imageList = imageRepository.findAllByProductId(productId);
        List<ImgList> imgListsDto = imageList.stream().map(ImageMapper.INSTANCE::ImageEntityToDto).toList();
        if (!product.getIsLimitedEdition()) {
            ProductDetail productDetail = ProductDetail.builder().name(product.getName())
                    .price(product.getPrice()).stock(product.getStock()).description(product.getDescription())
                    .imglist(imgListsDto).build();
            return ResponseEntity.status(HttpStatus.OK).body(productDetail);
        } else {
            boolean isOpen = product.getOpenDateTime().isBefore(LocalDateTime.now());
            ProductDetail productDetail = ProductDetail.builder().name(product.getName())
                    .price(product.getPrice()).description(product.getDescription()).isLimitedEdition(true)
                    .isOpen(isOpen).openDateTime(product.getOpenDateTime())
                    .imglist(imgListsDto).build();
            return ResponseEntity.status(HttpStatus.OK).body(productDetail);
        }
    }


    @Transactional
    public void editProduct(Long productId, EditProduct editProduct) {
        Product product = findProductById(productId);
        if (editProduct.getDescription() != null) {
            product.setDescription(editProduct.getDescription());
        }
        if (editProduct.getPrice() != 0) {
            product.setPrice(editProduct.getPrice());
        }

        if (editProduct.getStock() != 0) {
            product.setStock(editProduct.getStock());
        }
        if (editProduct.getImageUrl() != null) {
            Image image = Image.builder().imgUrl(editProduct.getImageUrl()).name(editProduct.getImageName())
                    .productId(productId).build();
            if (editProduct.getIsThumbnail()) {
                product.setThumbnailName(editProduct.getImageName());
                product.setThumbnailUrl(editProduct.getImageUrl());
            }
            imageRepository.save(image);
        }
        productRepository.save(product);
    }

    @Transactional
    public List<ProductFeignResponse> findProduct(long[] productIdList) {
        ArrayList<ProductFeignResponse> productList = new ArrayList<>();
        for (Long productId : productIdList) {
            Product product = findProductById(productId);
            productList.add(new ProductFeignResponse(product.getProductId(), product.getName(), product.getPrice(), product.getThumbnailUrl(), product.getThumbnailName()));
        }
        return productList;
    }

    @Transactional
    public List<CartProductFeignResponse> findCartProduct(long[] productIdList) {
        ArrayList<CartProductFeignResponse> productList = new ArrayList<>();
        for (Long productId : productIdList) {
            Product product = findProductById(productId);
            productList.add(new CartProductFeignResponse(product.getProductId(), product.getName(), product.getPrice()));
        }
        return productList;
    }

    @Transactional
    @DistributedLock(key = "#productId")
    @Cacheable(value = "productStockCache",key = "#productId",cacheManager = "redisCacheManager")
    public ProductStockDetail getProductStock(Long productId) {
        Product product = findProductById(productId);
        return new ProductStockDetail(product.getProductId(), product.getName(), product.getStock(),false);
    }

    private Product findProductById(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId).orElseThrow(() -> new NotFoundException("존재하지 않는 물품입니다."));
    }


    /**
     * 밑에는 write-back 로직
     */

    @Transactional
    @DistributedLock(key = "#productId")
    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
    public ProductStockDetail minusStock(String productId, OrderProductCountFeignRequest orderProduct) {
            ProductStockDetail productStockDetail = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+orderProduct.productId());
            int changedStock = 0;
            if(productStockDetail!=null) {
                if(productStockDetail.getStock()==0)  throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + orderProduct.productId());
                changedStock = productStockDetail.getStock() - orderProduct.count();
                log.info("[재고 감소 로직 실행중] 현재 재고: {}",changedStock);
            }
            else{ // cache에 데이터가 없다는건 유효기간이 돼서 사라졌고, db는 업데이트 된 정보이므로 db에서 꺼내서 재고 적용
                Product product = findProductById(orderProduct.productId());
                if(product.getStock()==0) throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + orderProduct.productId());
                changedStock = product.getStock() - orderProduct.count();
            }
            return ProductStockDetail.builder().productId(orderProduct.productId())
                .modified(true).stock(changedStock).build();
    }

    @Transactional
    @DistributedLock(key = "#productId")
    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
    public ProductStockDetail plusStock(String productId, OrderProductCountFeignRequest orderProduct) {
        ProductStockDetail productStockDetail = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+orderProduct.productId());
        int changedStock = 0;
        if(productStockDetail!=null) {
            changedStock = productStockDetail.getStock() + orderProduct.count();
        }
        else{
            Product product = findProductById(orderProduct.productId());
            changedStock = product.getStock() + orderProduct.count();
        }
        log.info("[재고 증가 로직 실행중] 현재 재고: {}",changedStock);
        return ProductStockDetail.builder().productId(orderProduct.productId())
                .modified(true).stock(changedStock).build();
    }


    /**
     * 장바구니에 있던 물품들을 주문하고, 주문 취소한 경우(반품 등) => 한 order에 물품이 여러개
     */

    @Transactional
    public void minusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount) { // 장바구니에서 한꺼번에 시키는 경우 재고 감소(장바구니 로직)
        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
            ProductStockDetail stockDetail = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+orderProduct.productId());
            if(stockDetail!=null) { // 캐시된 재고가 있는 경우 db와 캐시된 데이터는 같지않을 수 있으므로, 캐시 데이터에서 변동시켜준 값을 넣어줘야함
                int changeStock = stockDetail.getStock()-orderProduct.count();
                if (changeStock < 0) throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + orderProduct.productId());
                ProductStockDetail productDetail = ProductStockDetail.builder().stock(changeStock)
                        .productId(orderProduct.productId()).modified(true).build();
                redisTemplate.opsForValue().set(String.valueOf(productDetail.getProductId()),productDetail);
            }
            else{
                // 캐시된 재고가 없다면 db의 값이 동기화된 값이므로 db값에서 변동재고를 가감한 값을 캐시에 넣어주면됨
                Product product = findProductById(orderProduct.productId());
                int changeStock = product.getStock()- orderProduct.count();
                if (changeStock < 0) throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + orderProduct.productId());
                ProductStockDetail productDetail = ProductStockDetail.builder().stock(changeStock)
                        .productId(orderProduct.productId()).modified(true).build();
                redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(product.getProductId()),productDetail);
            }
        }
    }


    @Transactional
    public void plusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
            ProductStockDetail productStockDetailBefore = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+String.valueOf(orderProduct.productId()));
            int changedStock = 0;
            if(productStockDetailBefore!=null){
                changedStock = productStockDetailBefore.getStock() + orderProduct.count();
            }
            else {
                Product product = findProductById(orderProduct.productId());
                changedStock = product.getStock() + orderProduct.count();
            }

            // write-back 전략을 사용한다면 cache만 수정되었음을 표시하기 위해 modified필드 true
            ProductStockDetail productStockDetailAfter = ProductStockDetail.builder().stock(changedStock)
                    .productId(orderProduct.productId()).modified(true).build();
            // redis에 직접 저장하기. redis의 key -> productId
            redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(productStockDetailAfter.getProductId()),productStockDetailAfter);
        }
    }


    /**
     * 밑에부터는 스케쥴러의 cache 삭제 db 업데이트 로직
     */
    public void synchronizeDB() {
        Set<String> redisKeys = getKeysWithPattern("productStockCache::*");
        List<ProductStockDetail> productToSync = new ArrayList<>();
        if(redisKeys !=null && !redisKeys.isEmpty()){
            for(String redisKey : redisKeys) {
                log.info("redisKey == {}", redisKey);
                ProductStockDetail product = (ProductStockDetail) redisTemplate.opsForValue().get(redisKey);
                log.info("redisTemplate Response Product : {}", product);
                if (product != null && product.getModified()) {
                    productToSync.add(product); // db에서 캐싱된 이후로 수정이 된 캐시라면 db와 다르므로 동기화해야하는 리스트에 넣어줌
                }
            }
            String[] keysToDelete = redisKeys.toArray(new String[0]);
            redisTemplate.delete(Arrays.asList(keysToDelete));
        }
        productSynchronize(productToSync);
    }

    private Set<String> getKeysWithPattern(String pattern){
        Set<String> keys = new HashSet<>();
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(200).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            }catch (Exception e){
                throw new IllegalStateException();
            }
            return null;
        });
        return keys;
    }

    @Transactional
    public void productSynchronize(List<ProductStockDetail> productStock){
        List<Product> productList = new ArrayList<>();
        for(ProductStockDetail product:productStock){
            Product targetProduct = findProductById(product.getProductId());
            targetProduct.setStock(product.getStock()); // db의 재고를 cache된 재고로 변경 작업
            productList.add(targetProduct);
        }
        productRepository.saveAll(productList);
    }

    public void existProduct(Long productId) {
        boolean isExistProduct = productRepository.existsById(productId);
        if(!isExistProduct) throw new NotFoundException("존재하지 않는 물품입니다. 물품 번호(productId:"+productId+")");
    }


    /**
     * write-through 전략 사용한 로직
     */
//    @Transactional
////    @DistributedLock(key = "#productId")
////    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
//    public ProductStockDetail minusStock(String productId, OrderProductCountFeignRequest orderProduct) {
//        Product product = findProductById(orderProduct.productId());
//        int changedStock = product.getStock() - orderProduct.count();
//        if (changedStock < 0)
//            throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + product.getProductId());
//        product.setStock(changedStock);
//        productRepository.save(product);
//        return ProductStockDetail.builder().productId(product.getProductId())
//                .productName(product.getName()).modified(true).stock(changedStock).build();
//    }
//
//
//    @Transactional
////    @DistributedLock(key = "#productId")
////    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
//    public ProductStockDetail plusStock(String productId, OrderProductCountFeignRequest orderProduct) {
//        Product product = findProductById(orderProduct.productId());
//        int changedStock = product.getStock() + orderProduct.count();
//        product.setStock(changedStock);
//        productRepository.save(product);
//        log.info("[재고 증가 로직 실행중] 현재 재고: {}",changedStock);
//        return ProductStockDetail.builder().productId(orderProduct.productId())
//                .modified(true).stock(changedStock).build();
//    }


//    @Transactional
//    public void minusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount) { // 장바구니에서 한꺼번에 시키는 경우 재고 감소(장바구니 로직)
//        ArrayList<Product> products = new ArrayList<>();
//        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
//            Product product = findProductById(orderProduct.productId());
//            int changedStock = product.getStock() - orderProduct.count();
//            if (changedStock < 0)
//                throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + product.getProductId());
//            product.setStock(changedStock);
//            products.add(product);
//            ProductStockDetail productStockDetail = ProductStockDetail.builder().stock(changedStock)
//                    .productId(orderProduct.productId()).modified(true).build();
//            redisTemplate.opsForValue().set("productStockCache::" + String.valueOf(product.getProductId()), productStockDetail);
//        }
//        productRepository.saveAll(products);
//    }

//    @Transactional
//    public void plusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
//        ArrayList<Product> products = new ArrayList<>();
//        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
//            Product product = findProductById(orderProduct.productId());
//            int changedStock = product.getStock() + orderProduct.count();
//            // todo 만약 write-back 전략이면 db와 캐시 정보가 다를수도 있으므로, 캐시가 있다면 캐시에서 재고를 변동시켜준 값을 저장해야하고, 캐시가 없으면 db에서 재고를 변동시켜준 값을 저장해야함
//            // write-back 전략을 사용한다면 cache만 수정되었음을 표시하기 위해 modified필드 true
//            product.setStock(changedStock);
//            products.add(product);
//            ProductStockDetail productStockDetailAfter = ProductStockDetail.builder().stock(changedStock)
//                    .productId(orderProduct.productId()).modified(true).build();
//            // redis에 직접 저장하기. redis의 key -> productId
//            redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(productStockDetailAfter.getProductId()),productStockDetailAfter);
//        }
//        productRepository.saveAll(products);
//    }
}

