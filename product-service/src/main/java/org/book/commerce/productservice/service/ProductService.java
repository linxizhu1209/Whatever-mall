package org.book.commerce.productservice.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.productservice.dto.*;
import org.book.commerce.productservice.repository.ImageRepository;
import org.book.commerce.productservice.repository.ProductRepository;
import org.book.commerce.productservice.domain.Image;
import org.book.commerce.productservice.domain.Product;
import org.book.commerce.productservice.service.mapper.ImageMapper;
import org.book.commerce.productservice.service.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private final ImageUploadService imageUploadService;
    public ResponseEntity addProduct(AddProductDto addProductDto) {
        Product product = Product.builder().stock(addProductDto.getStock()).name(addProductDto.getName()).price(addProductDto.getPrice())
                .description(addProductDto.getDescription())
                .thumbnailName(addProductDto.getImageName()).thumbnailUrl(addProductDto.getImageUrl()).build(); // 이후 어떤 관리자가 올렸는지 관리자 id도 저장할 예정
        Long productId = productRepository.save(product).getProductId();
        imageUploadService.upload(productId,addProductDto.getImageName(),addProductDto.getImageUrl());
        return ResponseEntity.status(HttpStatus.OK).body("물품 추가가 완료되었습니다!");
    }

    public ResponseEntity<List<AllProductList>> getProducts() {
        List<Product> productlist = productRepository.findAll();
        List<AllProductList> productDtoList = productlist.stream().map(ProductMapper.INSTANCE::ProductEntityToDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(productDtoList);
    }

    public ResponseEntity<ProductDetail> getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()->new NotFoundException("일치하는 제품을 찾을 수 없습니다. 에러 발생 물품 번호:"+productId));
        List<Image> imageList = imageRepository.findAllByProductId(productId);
        List<ImgList> imgListsDto = imageList.stream().map(ImageMapper.INSTANCE::ImageEntityToDto).toList();
        ProductDetail productDetail = ProductDetail.builder().name(product.getName())
                .price(product.getPrice()).stock(product.getStock()).description(product.getDescription())
                .imglist(imgListsDto).build();
        return ResponseEntity.status(HttpStatus.OK).body(productDetail);
    }


    public ResponseEntity editProduct(Long productId, EditProduct editProduct) {
        Product product = productRepository.findById(productId).orElseThrow(()->new NotFoundException("일치하는 제품을 찾을 수 없습니다. 에러 발생 물품 번호:"+productId));
        if(editProduct.getDescription()!=null){
            product.setDescription(editProduct.getDescription());
        }
        if(editProduct.getPrice()!=0){
            product.setPrice(editProduct.getPrice());
        }
        if(editProduct.getImageUrl()!=null){
          Image image = Image.builder().imgUrl(editProduct.getImageUrl()).name(editProduct.getImageName())
                  .productId(productId).build();
          if(editProduct.getIsThumbnail()){
              product.setThumbnailName(editProduct.getImageName());
              product.setThumbnailUrl(editProduct.getImageUrl());
          }
          imageRepository.save(image);
        }
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body("상품 수정이 완료되었습니다.");
    }

    public List<ProductFeignResponse> findProduct(long[] productIdList){
        ArrayList<ProductFeignResponse> productList = new ArrayList<>();
        for(Long productId:productIdList) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("존재하지 않는 물품입니다."));
            productList.add(new ProductFeignResponse(product.getProductId(),product.getName(),product.getPrice(), product.getThumbnailUrl(),product.getThumbnailName()));
        }
        return productList;
    }

    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public List<CartProductFeignResponse> findCartProduct(long[] productIdList) {
        ArrayList<CartProductFeignResponse> productList = new ArrayList<>();
        for(Long productId:productIdList) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("존재하지 않는 물품입니다."));
            productList.add(new CartProductFeignResponse(product.getProductId(),product.getName(),product.getPrice()));
        }
        return productList;
    }

    public void minusStock(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
        ArrayList<Product> products = new ArrayList<>();
        for(OrderProductCountFeignRequest orderProduct:orderProductCount){
            // todo product찾아오는 로직 통합예정
            Product product = productRepository.findById(orderProduct.productId()).orElseThrow();
            int changedStock = product.getStock()-orderProduct.count();
            if(changedStock<0) throw new ConflictException("주문하신 상품의 재고가 부족하여 구매를 할 수 없습니다. 확인해주세요. 상품 번호: "+product.getProductId());
            product.setStock(changedStock);
            products.add(product);
        }
        productRepository.saveAll(products);
    }

    public void plusStock(ArrayList<OrderProductCountFeignRequest> orderProductCount) {
        ArrayList<Product> products = new ArrayList<>();
        for(OrderProductCountFeignRequest orderProduct:orderProductCount){
            // todo product찾아오는 로직 통합예정
            Product product = productRepository.findById(orderProduct.productId()).orElseThrow();
            int changedStock = product.getStock()+orderProduct.count();
            product.setStock(changedStock);
            products.add(product);
        }
        productRepository.saveAll(products);
    }
}
