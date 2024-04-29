package org.book.commerce.cartservice.service;

import org.book.commerce.cartservice.dto.CartProductFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="application-product", path = "/product")
public interface CartProductFeignClient {

    @GetMapping("/cartProduct")
    List<CartProductFeignResponse> findCartProductByProductId(@RequestParam("productId") final long[] productIdList);

}
