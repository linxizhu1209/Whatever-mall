package org.book.commerce.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
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

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final RedisTemplate redisTemplate;

    private final ImageUploadService imageUploadService;

    public ResponseEntity addProduct(AddProductDto addProductDto) {
        Product product = Product.builder().stock(addProductDto.getStock()).name(addProductDto.getName()).price(addProductDto.getPrice())
                .description(addProductDto.getDescription())
                .thumbnailName(addProductDto.getImageName()).thumbnailUrl(addProductDto.getImageUrl()).isLimitedEdition(addProductDto.getIsLimitedEdition())
                .openDateTime(addProductDto.getOpenDateTime()).build(); // 이후 어떤 관리자가 올렸는지 관리자 id도 저장할 예정
        Long productId = productRepository.save(product).getProductId();
        imageUploadService.upload(productId, addProductDto.getImageName(), addProductDto.getImageUrl());
        return ResponseEntity.status(HttpStatus.OK).body("물품 추가가 완료되었습니다!");
    }

    public ResponseEntity<List<AllProductList>> getProducts() {
        List<Product> productlist = productRepository.findAll();
        List<AllProductList> productDtoList = productlist.stream().map(ProductMapper.INSTANCE::ProductEntityToDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(productDtoList);
    }

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

        // todo 만약 한정판 제품이라면, 오픈시간이 지나지 않았으면 주문하기 버튼이 활성화되지 않아야함
        // todo 한정판 제품이면서 오픈시간이 지낫다면 isOpen을 true로, 오픈시간이 지나지않았다면 false로 반환
        // 만약 한정판 제품이 아니라면, 항상 open이 되도록 즉, isOpen이 기본 true로 되도록 설정
        // 만약 open이 false라면 오픈시간까지 적어서 return, 그게 아니라면 한정판 여부만 보내주기(기본은 false임)
    }


    public ResponseEntity editProduct(Long productId, EditProduct editProduct) {
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
        return ResponseEntity.status(HttpStatus.OK).body("상품 수정이 완료되었습니다.");
    }

    public List<ProductFeignResponse> findProduct(long[] productIdList) {
        ArrayList<ProductFeignResponse> productList = new ArrayList<>();
        for (Long productId : productIdList) {
            Product product = findProductById(productId);
            productList.add(new ProductFeignResponse(product.getProductId(), product.getName(), product.getPrice(), product.getThumbnailUrl(), product.getThumbnailName()));
        }
        return productList;
    }

    public List<CartProductFeignResponse> findCartProduct(long[] productIdList) {
        ArrayList<CartProductFeignResponse> productList = new ArrayList<>();
        for (Long productId : productIdList) {
            Product product = findProductById(productId);
            productList.add(new CartProductFeignResponse(product.getProductId(), product.getName(), product.getPrice()));
        }
        return productList;
    }

    @Transactional
    public void minusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
        ArrayList<Product> products = new ArrayList<>();
         for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
            Product product = findProductById(orderProduct.productId());
            int changedStock = product.getStock() - orderProduct.count();
            if (changedStock < 0)
                throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + product.getProductId());
            product.setStock(changedStock);
            products.add(product);
            ProductStockDetail productStockDetail = ProductStockDetail.builder().stock(changedStock)
                    .productId(orderProduct.productId()).modified(true).build();
            productRepository.saveAll(products);
            redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(product.getProductId()),productStockDetail);
            /**
             * 위 까지는 db에 저장되는 경우. db와 캐시 데이터는 일치하므로 redis에서 현재 재고를 꺼내올 필요가 없음
             */

            // todo 만약 write-back 전략이면 db와 캐시 정보가 다를수도 있으므로, 캐시가 있다면 캐시에서 재고를 변동시켜준 값을 저장해야하고, 캐시가 없으면 db에서 재고를 변동시켜준 값을 저장해야함
//
//            ProductStockDetail stockDetail = (ProductStockDetail) redisTemplate.opsForValue().get(orderProduct.productId()); // todo key가 productId와 일치하는지 확인해봐야함
//            if(stockDetail!=null) { // 캐시된 재고가 있는 경우 db와 캐시된 데이터는 같지않을 수 있으므로, 캐시 데이터에서 변동시켜준 값을 넣어줘야함
//                int nowStock = stockDetail.getStock();
//                int changeStock = nowStock-orderProduct.count();
//                ProductStockDetail productDetail = ProductStockDetail.builder().stock(changeStock)
//                        .productId(orderProduct.productId()).modified(true).build();
//                redisTemplate.opsForValue().set(String.valueOf(product.getProductId()),productDetail);
//            }
//            else{
//                // 캐시된 재고가 없다면 db의 값이 동기화된 값이므로 db값에서 변동재고를 가감한 값을 캐시에 넣어주면됨
//                ProductStockDetail productDetail = ProductStockDetail.builder().stock(changedStock)
//                        .productId(orderProduct.productId()).modified(true).build();
//                redisTemplate.opsForValue().set(String.valueOf(product.getProductId()),productDetail);
//            }
        }
    }

    /**
     * write-through인 경우 밑의 메서드를 사용하면 됨
     * cache 업데이트와 동시에 db업데이트 진행
     */
    @Transactional
    public void plusStock(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
        ArrayList<Product> products = new ArrayList<>();
        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
            Product product = findProductById(orderProduct.productId());
            int changedStock = product.getStock() + orderProduct.count();
            product.setStock(changedStock);
            products.add(product);

            ProductStockDetail productStockDetail = ProductStockDetail.builder().stock(changedStock)
                    .productId(orderProduct.productId()).modified(true).build();
            // redis에 직접 저장하기. redis의 key -> productId
            redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(product.getProductId()),productStockDetail);

        }
        productRepository.saveAll(products);
    }

    /**
     * - write-back 전략을 썻을 경우에 밑의 주석을 풀면됨.
     * - db에 저장하지 않고 cache된 데이터를 변경하는 로직임. 만약 cache가 없다면 db가 최신정보이므로 이걸 변경해서 캐싱
     */
//    @Transactional
//    public void plusStock(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
//        ArrayList<Product> products = new ArrayList<>();
//        for (OrderProductCountFeignRequest orderProduct : orderProductCount) {
//            ProductStockDetail productStockDetailBefore = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+String.valueOf(orderProduct.productId());
//            int changedStock = 0;
//            if(productStockDetailBefore!=null){
//                changedStock = productStockDetailBefore.getStock() - orderProduct.count();
//            }
//            else {
//                Product product = findProductById(orderProduct.productId());
//                changedStock = product.getStock() + orderProduct.count();
//            }
//
//            // todo 만약 write-back 전략이면 db와 캐시 정보가 다를수도 있으므로, 캐시가 있다면 캐시에서 재고를 변동시켜준 값을 저장해야하고, 캐시가 없으면 db에서 재고를 변동시켜준 값을 저장해야함
//            // write-back 전략을 사용한다면 cache만 수정되었음을 표시하기 위해 modified필드 true
//            ProductStockDetail productStockDetailAfter = ProductStockDetail.builder().stock(changedStock)
//                    .productId(orderProduct.productId()).modified(true).build();
//            // redis에 직접 저장하기. redis의 key -> productId
//            redisTemplate.opsForValue().set("productStockCache::"+String.valueOf(productStockDetailAfter.getProductId()),productStockDetailAfter);
//        }
//     }
//

    @Transactional
    @Cacheable(value = "productStockCache",key = "#productId",cacheManager = "redisCacheManager")
    public ProductStockDetail getProductStock(Long productId) {
        Product product = findProductById(productId);
        return new ProductStockDetail(product.getProductId(), product.getName(), product.getStock(),false);
        // todo 누군가가 결제프로세스에 들어가서 재고가 감소하는 쓰레드가 진행중이라면, 재고 조회를 하지 못해야함
    }

    private Product findProductById(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId).orElseThrow(() -> new NotFoundException("존재하지 않는 물품입니다."));
    }

    /**
     * db와 cache 동시 업데이트 로직
     */
//    @Transactional
//    @CachePut(cacheNames = "Product",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
//    public ProductStockDetail minusStock(OrderProductCountFeignRequest orderProduct) {
//        Product product = findProductById(orderProduct.productId());
//        int changedStock = product.getStock() - orderProduct.count();
//        if (changedStock < 0)
//            throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + product.getProductId());
//        product.setStock(changedStock);
//        productRepository.save(product);
//        return ProductStockDetail.builder().productId(product.getProductId())
//                .productName(product.getName()).modified(true).stock(changedStock).build();
//    }

    /**
     * cache에 업데이트 후 나중에 db업데이트 로직
     */
    @Transactional
    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
    public ProductStockDetail minusStock(OrderProductCountFeignRequest orderProduct) {
        Product product = findProductById(orderProduct.productId());
        ProductStockDetail productStockDetail = (ProductStockDetail) redisTemplate.opsForValue().get("productStockCache::"+product.getProductId());
        int changedStock = 0;
        if(productStockDetail!=null) {
            changedStock = productStockDetail.getStock() - orderProduct.count();
        }
        else{ // cache에 데이터가 없다는건 유효기간이 돼서 사라졌고, db는 업데이트 된 정보이므로 db에서 꺼내서 재고 적용
            changedStock = product.getStock() - orderProduct.count();
        }
        if (changedStock < 0)
            throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: " + product.getProductId());
        return ProductStockDetail.builder().productId(product.getProductId())
                .productName(product.getName()).modified(true).stock(changedStock).build();
    }


    @Transactional
    @CachePut(value = "productStockCache",key = "#orderProduct.productId", cacheManager = "redisCacheManager")
    public ProductStockDetail plusStock(OrderProductCountFeignRequest orderProduct) {
        Product product = findProductById(orderProduct.productId());
        ProductStockDetail productStockDetail = (ProductStockDetail) redisTemplate.opsForValue().get(product.getProductId());
        int changedStock = 0;
        if(productStockDetail!=null) {
            changedStock = productStockDetail.getStock() + orderProduct.count();
        }
        else{ // cache에 데이터가 없다는건 유효기간이 돼서 사라졌고, db는 업데이트 된 정보이므로 db에서 꺼내서 재고 적용
            changedStock = product.getStock() + orderProduct.count();
        }
        return ProductStockDetail.builder().productId(product.getProductId())
                .productName(product.getName()).modified(true).stock(changedStock).build();
    }

    /** 캐시가 30분마다 db에 업데이트하고 삭제하기.(스케줄러 활용)
     * => 위와 같이 한 이유는 만약 업데이트 시간을 15분 10분 이렇게 삭제시간과 다르게 둔다면,
     * 그 사이에 캐시가 업데이트되었다면 db에는 그 업데이트가 반영되지 않기때문
     *
     */
    /**
     * 밑에부터는 스케쥴러의 cache 삭제 db 업데이트 로직!
     *
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
            targetProduct.setStock(product.getStock());
            productList.add(targetProduct);
        }
        productRepository.saveAll(productList);
    }

}
