package org.book.commerce.productservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.productservice.dto.*;
import org.book.commerce.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    @PostMapping("/admin/add")
    public ResponseEntity<ResponseAddProduct> addProduct(@RequestBody AddProductDto addProductDto){
        ResponseAddProduct response = productService.addProduct(addProductDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/allproduct")
    public ResponseEntity<List<AllProductList>> getAllProduct(){
        return productService.getProducts();
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDetail> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }
    @GetMapping("/detailStock/{productId}")
    public ResponseEntity<ProductStockDetail> getStockDetail(@PathVariable Long productId){
        ProductStockDetail productStockDetail = productService.getProductStock(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productStockDetail);
    }

    @PutMapping("/admin/edit/{productId}")
    public ResponseEntity<String> editProdcut(@PathVariable Long productId,@RequestBody EditProduct editProduct){
        productService.editProduct(productId,editProduct);
        return ResponseEntity.status(HttpStatus.OK).body("상품 수정이 완료되었습니다.");
    }

    @GetMapping()
    public List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList){
        log.info("[User->Product] open feign 통신이 성공하였습니다");
        return productService.findProduct(productIdList);
    }

    @GetMapping("/cartProduct")
    public List<CartProductFeignResponse> findCartProductByProductId(@RequestParam("productId") final long[] productIdList){
        log.info("[Cart->Product] open feign 통신이 성공하였습니다");
        return productService.findCartProduct(productIdList);
    }

    @PutMapping("/minusStockList")
    public ResponseEntity<String> minusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount){
        log.info("[Order->Product] open feign 통신이 성공하였습니다");
        productService.minusStockList(orderProductCount);
        return ResponseEntity.status(HttpStatus.OK).body("재고가 성공적으로 변경되었습니다!");
    }

    @PutMapping("/plusStockList")
    public ResponseEntity<String> plusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount){
        log.info("[Order->Product] open feign 통신이 성공하였습니다");
        productService.plusStockList(orderProductCount);
        return ResponseEntity.status(HttpStatus.OK).body("재고가 성공적으로 변경되었습니다!");
    }

    @PutMapping("/minusStock/{productId}")
    public ResponseEntity<String> minusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest){
        log.info("[Order->Product] open feign 통신이 성공하였습니다");
        productService.minusStock(String.valueOf(productId),orderProductCountFeignRequest);
        return ResponseEntity.status(HttpStatus.OK).body("재고가 성공적으로 변경되었습니다.");
    }

    @PutMapping("/plusStock/{productId}")
    public ResponseEntity<String> plusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest){
        log.info("[Order->Product] open feign 통신이 성공하였습니다");
        productService.plusStock(String.valueOf(productId),orderProductCountFeignRequest);
        return ResponseEntity.status(HttpStatus.OK).body("재고가 성공적으로 변경되었습니다.");
    }

    @GetMapping("/isExistProduct/{productId}")
    public ResponseEntity<String> isExistProduct(@PathVariable Long productId){
        log.info("[User/Cart->Product] open feign 통신이 성공하였습니다");
        productService.existProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("물품이 존재합니다.");
    }
}
