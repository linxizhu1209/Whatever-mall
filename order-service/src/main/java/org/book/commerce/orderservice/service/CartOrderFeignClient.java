package org.book.commerce.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.book.commerce.orderservice.dto.CartOrderFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CircuitBreaker(name="circuit")
@FeignClient(name="application-cart", path = "/cart")
public interface CartOrderFeignClient {

    @GetMapping("/findCart")
    List<CartOrderFeignResponse> findCartListByUserEmail(@RequestParam("userEmail") String userEmail);

    @DeleteMapping("/deleteAll")
    ResponseEntity deleteAllCart(@RequestParam("userEmail") String userEmail);

}
