package org.book.commerce.cartservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.cartservice.dto.AddCartResult;
import org.book.commerce.cartservice.dto.CartListDto;
import org.book.commerce.cartservice.dto.CartOrderFeignResponse;
import org.book.commerce.cartservice.service.CartService;
import org.book.commerce.common.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<AddCartResult> addCart(@RequestHeader("X-Authorization-Id") String userEmail,
                                                 @PathVariable Long productId, @RequestParam int count){
        AddCartResult addCartResult = cartService.addCart(userEmail,productId,count);
        return ResponseEntity.status(HttpStatus.OK).body(addCartResult);
    }

    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<CommonResponseDto> deleteCart(@PathVariable Long cartId){
        CommonResponseDto response = cartService.deleteCart(cartId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<CommonResponseDto> updateCart(@PathVariable Long cartId,
                                     @RequestParam int count){
        CommonResponseDto response = cartService.updateCart(cartId,count);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getCart")
    public ResponseEntity<List<CartListDto>> getCartlist(@RequestHeader("X-Authorization-Id") String userEmail){
        List<CartListDto> cartListDto = cartService.getCartList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(cartListDto);
    }

    @GetMapping("/findCart")
    public ArrayList<CartOrderFeignResponse> findCartListByUserEmail(@RequestParam("userEmail") String userEmail){
        log.info("[OrderService-CartService] open feign 통신이 성공하였습니다");
        return cartService.findCartList(userEmail);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllCart(@RequestParam("userEmail") String userEmail){
        log.info("[OrderService-CartService] open feign 통신이 성공하였습니다");
        cartService.deleteAllCart(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 목록이 정상적으로 삭제되었습니다!");
    }

}
