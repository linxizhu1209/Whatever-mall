package org.book.commerce.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.book.commerce.productservice.dto.AddProductDto;
import org.book.commerce.productservice.dto.AllProductList;
import org.book.commerce.productservice.dto.EditProduct;
import org.book.commerce.productservice.dto.ProductDetail;
import org.book.commerce.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name="물품 API",description = "물품을 조회할 수 있는 API입니다")
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    @PostMapping("/admin/add")//,consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "관리자의 물품 등록",description = "관리자가 묾품을 등록한다")
    public ResponseEntity addProduct(@Valid @RequestBody AddProductDto addProductDto){ //@RequestPart(name="productImage", required = false) MultipartFile productImages)     {
        return productService.addProduct(addProductDto);
    }

    @GetMapping("")
    @Operation(summary = "모든 물품 조회",description = "모든 사람이 물품을 조회할 수 있다")
    public ResponseEntity<List<AllProductList>> getAllProduct(){
        return productService.getProducts();
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "물품 상세 조회",description = "물품의 상세 정보를 조회 한다")
    public ResponseEntity<ProductDetail> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }

    @PutMapping("/admin/edit/{productId}")
    @Operation(summary = "관리자의 물품 수정",description = "관리자가 물품의 정보를 수정한다")
    public ResponseEntity editProdcut(@PathVariable Long productId,@RequestBody EditProduct editProduct){
        return productService.editProduct(productId,editProduct);
    }

}
