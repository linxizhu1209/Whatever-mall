package org.book.commerce.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.productservice.dto.*;
import org.book.commerce.productservice.service.ProductService;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name="물품 API",description = "물품을 조회하고 등록할 수 있는 API입니다")
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    @PostMapping("/admin/add")//,consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "관리자의 물품 등록",description = "관리자가 묾품을 등록한다")
    public ResponseEntity<String> addProduct(@Valid @RequestBody AddProductDto addProductDto){ //@RequestPart(name="productImage", required = false) MultipartFile productImages)     {
        productService.addProduct(addProductDto);
        return ResponseEntity.status(HttpStatus.OK).body("상품 추가가 완료되었습니다!");
    }

    @GetMapping("/allproduct")
    @Operation(summary = "모든 물품 조회",description = "모든 사람이 물품을 조회할 수 있다")
    public ResponseEntity<List<AllProductList>> getAllProduct(){
        return productService.getProducts();
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "물품 상세 조회",description = "물품의 상세 정보를 조회 한다")
    public ResponseEntity<ProductDetail> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }
    @GetMapping("/detailStock/{productId}")
    @Operation(summary = "물품의 재고 상세조회",description = "물품 구매전 물품의 재고를 확인할 수 있는 창입니다")
    public ResponseEntity<ProductStockDetail> getStockDetail(@PathVariable Long productId){
        ProductStockDetail productStockDetail = productService.getProductStock(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productStockDetail);
    }

    @PutMapping("/admin/edit/{productId}")
    @Operation(summary = "관리자의 물품 수정",description = "관리자가 물품의 정보를 수정한다")
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
//        throw new CommonException("실패!!!!!!!");
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


}
