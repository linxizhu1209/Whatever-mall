package org.book.commerce.userservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.book.commerce.userservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CircuitBreaker(name="circuit")
@FeignClient(name="application-product",path = "/product")
public interface ProductFeignClient {
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);

    @GetMapping("/isExistProduct/{productId}")
    ResponseEntity<String> isExistProduct(@PathVariable Long productId);
}
