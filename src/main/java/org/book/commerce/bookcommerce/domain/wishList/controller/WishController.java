package org.book.commerce.bookcommerce.domain.wishList.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.book.commerce.bookcommerce.domain.wishList.dto.WishListDto;
import org.book.commerce.bookcommerce.domain.wishList.service.WishService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;
    @PostMapping("/add/{productId}")
    public ResponseEntity addWish(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long productId){
        log.info("[WishController] 찜 목록 추가 요청이 들어왔습니다");
        return wishService.addWish(customUserDetails,productId);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity deleteWish(@PathVariable Long productId){
        log.info("[WishController] 찜 목록 삭제 요청이 들어왔습니다");
        return wishService.deleteWish(productId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<WishListDto>> getWishList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("[WishController] 찜 목록 조회 요청이 들어왔습니다");
        return wishService.getWishList(customUserDetails);
    }

}
