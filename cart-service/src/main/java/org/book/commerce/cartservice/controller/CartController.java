package org.book.commerce.cartservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.cartservice.dto.AddCartResult;
import org.book.commerce.cartservice.dto.CartListDto;
import org.book.commerce.cartservice.dto.CartOrderFeignResponse;
import org.book.commerce.cartservice.service.CartService;
import org.book.commerce.common.security.CustomUserDetails;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name="장바구니 API",description = "물품을 장바구니에 담고 조회,삭제,수정할 수 있는 API입니다")
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    @Operation(summary = "장바구니 추가", description = "장바구니에 물품을 추가한다")
    @PostMapping("/add/{productId}")
    public ResponseEntity addCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @PathVariable Long productId, @RequestParam int count){
        AddCartResult addCartResult = cartService.addCart(customUserDetails,productId,count);
        return ResponseEntity.status(HttpStatus.OK).body(addCartResult);
    }

    @Operation(summary = "장바구니 삭제", description = "장바구니에 담긴 물품을 삭제한다")
    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity deleteCart(@PathVariable Long cartId){
        cartService.deleteCart(cartId);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 물품이 삭제되었습니다");
    }

    @Operation(summary = "장바구니 수정",description = "장바구니에 담긴 물품의 수량을 수정한다")
    @PutMapping("/update/{cartId}")
    public ResponseEntity updateCart(@PathVariable Long cartId,
                                     @RequestParam int count){
        cartService.updateCart(cartId,count);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 물품의 수량이 수정되었습니다");
    }

    @Operation(summary = "장바구니 조회",description = "장바구니에 담긴 물품들을 조회한다")
    @GetMapping("/getCart")
    public ResponseEntity<List<CartListDto>> getCartlist(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CartListDto> cartListDto = cartService.getCartList(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(cartListDto);
    }

    @GetMapping("/findCart")
    public List<CartOrderFeignResponse> findCartListByUserEmail(@RequestParam("userEmail") String userEmail){
        log.info("[OrderService-CartService] open feign 통신이 성공하였습니다");
        return cartService.findCartList(userEmail);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAllCart(@RequestParam("userEmail") String userEmail){
        log.info("[OrderService-CartService] open feign 통신이 성공하였습니다");
        cartService.deleteAllCart(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 목록이 정상적으로 삭제되었습니다!");
    }

}
