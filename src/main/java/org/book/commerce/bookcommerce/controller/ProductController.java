package org.book.commerce.bookcommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.controller.dto.AddProductDto;
import org.book.commerce.bookcommerce.controller.dto.AllProductList;
import org.book.commerce.bookcommerce.controller.dto.EditProduct;
import org.book.commerce.bookcommerce.controller.dto.ProductDetail;
import org.book.commerce.bookcommerce.repository.entity.CustomUserDetails;
import org.book.commerce.bookcommerce.repository.entity.Product;
import org.book.commerce.bookcommerce.service.exception.CustomUserDetailService;
import org.book.commerce.bookcommerce.service.exception.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
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
