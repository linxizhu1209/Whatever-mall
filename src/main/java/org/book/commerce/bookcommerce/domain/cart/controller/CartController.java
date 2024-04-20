package org.book.commerce.bookcommerce.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.domain.cart.dto.AddCartResult;
import org.book.commerce.bookcommerce.domain.cart.dto.CartListDto;
import org.book.commerce.bookcommerce.domain.cart.service.CartService;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    @PostMapping("/add/{productId}")
    public ResponseEntity addCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @PathVariable Long productId, @RequestParam int count){
        AddCartResult addCartResult = cartService.addCart(customUserDetails,productId,count);
        return ResponseEntity.status(HttpStatus.OK).body(addCartResult);
    }

    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity deleteCart(@PathVariable Long cartId){
        cartService.deleteCart(cartId);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 물품이 삭제되었습니다");
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity updateCart(@PathVariable Long cartId,
                                     @RequestParam int count){
        cartService.updateCart(cartId,count);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 물품의 수량이 수정되었습니다");
    }

    @GetMapping("/getCart")
    public ResponseEntity<List<CartListDto>> getCartlist(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CartListDto> cartListDto = cartService.getCartList(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(cartListDto);
    }
}
