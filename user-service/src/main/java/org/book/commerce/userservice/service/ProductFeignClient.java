package org.book.commerce.userservice.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.userservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="application-product",path = "/product")
public interface ProductFeignClient {

    @CircuitBreaker(name="productServiceCircuitBreaker", fallbackMethod = "fallbackFindProductByProductId")
    @Retry(name="productServiceRetry")
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);

    @GetMapping("/isExistProduct/{productId}")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackIsExistProduct")
    @Retry(name = "productServiceRetry")
    ResponseEntity<String> isExistProduct(@PathVariable Long productId);

    default List<ProductFeignResponse> fallbackFindProductByProductId(long[] productIdList, Throwable t) {
        // 오류 메시지 반환
        throw new CommonException("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.", ErrorCode.SERVICE_UNAVAILABLE);
    }

    default ResponseEntity<String> fallbackIsExistProduct(Long productId, Throwable t) {
        // 오류 메시지 반환
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }

}
