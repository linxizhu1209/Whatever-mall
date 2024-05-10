package org.book.commerce.orderservice.service;

//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.book.commerce.orderservice.domain.ProductOrder;
import org.book.commerce.orderservice.dto.OrderProductCountFeignRequest;
import org.book.commerce.orderservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//@CircuitBreaker(name="circuit")
@FeignClient(name="application-product", path = "/product")
public interface OrderProductFeignClient {
    @PutMapping("/minusStockList")
    ResponseEntity<String> minusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);

    @PutMapping("/plusStockList")
    ResponseEntity<String> plusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);

    @PutMapping("/minusStock/{productId}")
    ResponseEntity<String> minusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest);


    @PutMapping("/plusStock/{productId}")
    ResponseEntity<String> plusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest);
}
