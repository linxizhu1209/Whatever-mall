package org.book.commerce.bookcommerce.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.repository.ImageRepository;
import org.book.commerce.bookcommerce.repository.ProductRepository;
import org.book.commerce.bookcommerce.repository.entity.CustomUserDetails;
import org.book.commerce.bookcommerce.repository.entity.Image;
import org.book.commerce.bookcommerce.repository.entity.Product;
import org.book.commerce.bookcommerce.service.mapper.ImageMapper;
import org.book.commerce.bookcommerce.service.mapper.ProductMapper;
import org.book.commerce.bookcommerce.web.controller.dto.*;
import org.book.commerce.bookcommerce.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private final ImageUploadService imageUploadService;
    public ResponseEntity addProduct(CustomUserDetails customUserDetails, AddProductDto addProductDto) {
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
        Product product = productRepository.findById(productId).orElseThrow();
        List<Image> imageList = imageRepository.findAllByProductId(productId);
        List<ImgList> imgListsDto = imageList.stream().map(ImageMapper.INSTANCE::ImageEntityToDto).toList();
        ProductDetail productDetail = ProductDetail.builder().name(product.getName())
                .price(product.getPrice()).stock(product.getStock()).description(product.getDescription())
                .imglist(imgListsDto).build();
        return ResponseEntity.status(HttpStatus.OK).body(productDetail);
    }


    public ResponseEntity editProduct(Long productId, EditProduct editProduct) {
        Product product = productRepository.findById(productId).orElseThrow();
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
}
