package org.book.commerce.orderservice.service;

import org.book.commerce.orderservice.dto.OrderProductCountFeignRequest;
import org.book.commerce.orderservice.dto.ProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name="application-product", path = "/product")
public interface OrderProductFeignClient {
    @PutMapping("/minusStock")
    ResponseEntity minusStock(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);

    @PutMapping("/plusStock")
    ResponseEntity plusStock(@RequestBody ArrayList<OrderProductCountFeignRequest> orderProductCount);
    @GetMapping()
    List<ProductFeignResponse> findProductByProductId(@RequestParam("productId") final long[] productIdList);

}
