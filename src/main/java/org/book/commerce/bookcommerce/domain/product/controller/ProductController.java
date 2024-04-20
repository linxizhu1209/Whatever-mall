package org.book.commerce.bookcommerce.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.domain.product.dto.AddProductDto;
import org.book.commerce.bookcommerce.domain.product.dto.AllProductList;
import org.book.commerce.bookcommerce.domain.product.dto.EditProduct;
import org.book.commerce.bookcommerce.domain.product.dto.ProductDetail;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.book.commerce.bookcommerce.domain.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {


    private final ProductService productService;
    @PostMapping("/admin/add")//,consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @Valid @RequestBody AddProductDto addProductDto){ //@RequestPart(name="productImage", required = false) MultipartFile productImages)     {
        return productService.addProduct(customUserDetails,addProductDto);
    }

    @GetMapping("")
    public ResponseEntity<List<AllProductList>> getAllProduct(){
        return productService.getProducts();
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDetail> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }

    @PutMapping("/admin/edit/{productId}")
    public ResponseEntity editProdcut(@PathVariable Long productId,@RequestBody EditProduct editProduct){
        return productService.editProduct(productId,editProduct);
    }

}