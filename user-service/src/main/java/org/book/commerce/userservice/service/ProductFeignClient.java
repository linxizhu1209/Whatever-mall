package org.book.commerce.userservice.service;

import org.book.commerce.userservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="application-product",path = "/product")
public interface ProductFeignClient {
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);
}
