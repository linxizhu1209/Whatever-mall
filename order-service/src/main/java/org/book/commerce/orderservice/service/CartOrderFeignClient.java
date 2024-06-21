package org.book.commerce.orderservice.service;

//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.orderservice.dto.CartOrderFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@FeignClient(name="application-cart", path = "/cart")
public interface CartOrderFeignClient {
    @CircuitBreaker(name = "cartServiceCircuitBreaker", fallbackMethod = "fallbackFindCartListByUserEmail")
    @Retry(name = "cartServiceRetry")
    @GetMapping("/findCart")
    ArrayList<CartOrderFeignResponse> findCartListByUserEmail(@RequestParam("userEmail") String userEmail);

    @CircuitBreaker(name = "cartServiceCircuitBreaker", fallbackMethod = "fallbackDeleteAllCart")
    @Retry(name = "cartServiceRetry")
    @DeleteMapping("/deleteAll")
    ResponseEntity deleteAllCart(@RequestParam("userEmail") String userEmail);

    default ArrayList<CartOrderFeignResponse> fallbackFindCartListByUserEmail(String userEmail, Throwable t) {
        // 오류 메시지 반환 또는 기본값 반환
       throw new CommonException("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.", ErrorCode.SERVICE_UNAVAILABLE);
    }

    default ResponseEntity<String> fallbackDeleteAllCart(String userEmail, Throwable t) {
        // 오류 메시지 반환
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }





}
