package org.book.commerce.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.orderservice.dto.OrderProductCountFeignRequest;
import org.book.commerce.orderservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name="application-product", path = "/product")
public interface OrderProductFeignClient {
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackMinusStockList")
    @Retry(name = "productServiceRetry")
    @PutMapping("/minusStockList")
    ResponseEntity<String> minusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackPlusStockList")
    @Retry(name = "productServiceRetry")
    @PutMapping("/plusStockList")
    ResponseEntity<String> plusStockList(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackFindProductByProductId")
    @Retry(name = "productServiceRetry")
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackMinusStock")
    @Retry(name = "productServiceRetry")
    @PutMapping("/minusStock/{productId}")
    ResponseEntity<String> minusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest);

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackPlusStock")
    @Retry(name = "productServiceRetry")
    @PutMapping("/plusStock/{productId}")
    ResponseEntity<String> plusStock(@PathVariable Long productId, @RequestBody OrderProductCountFeignRequest orderProductCountFeignRequest);

    default ResponseEntity<String> fallbackMinusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }

    default ResponseEntity<String> fallbackPlusStockList(ArrayList<OrderProductCountFeignRequest> orderProductCount, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }

    default List<ProductFeignResponse> fallbackFindProductByProductId(long[] productIdList, Throwable t) {
        throw new CommonException("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.", ErrorCode.SERVICE_UNAVAILABLE);
    }

    default ResponseEntity<String> fallbackMinusStock(Long productId, OrderProductCountFeignRequest orderProductCountFeignRequest, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }

    default ResponseEntity<String> fallbackPlusStock(Long productId, OrderProductCountFeignRequest orderProductCountFeignRequest, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("지금 연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
    }
}
