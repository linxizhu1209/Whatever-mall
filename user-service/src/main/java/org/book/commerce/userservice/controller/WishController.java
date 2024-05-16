package org.book.commerce.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.userservice.dto.ProductFeignResponse;
import org.book.commerce.userservice.service.WishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;
    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addWish(@RequestHeader("X-Authorization-Id") String userEmail, @PathVariable Long productId){
        log.info("[WishController] 찜 목록 추가 요청이 들어왔습니다");
        wishService.addWish(userEmail,productId);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에 추가되었습니다!");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteWish(@PathVariable Long productId,@RequestHeader("X-Authorization-Id") String userEmail){
        log.info("[WishController] 찜 목록 삭제 요청이 들어왔습니다");
        wishService.deleteWish(userEmail,productId);
        return ResponseEntity.status(HttpStatus.OK).body("찜 목록에서 삭제되었습니다!");
    }
    @GetMapping("/list")
    public ResponseEntity<List<ProductFeignResponse>> getWishList(@RequestHeader("X-Authorization-Id") String userEmail){
        log.info("[WishController] 찜 목록 조회 요청이 들어왔습니다");
        List<ProductFeignResponse> productFeignResponseList = wishService.getWishList(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(productFeignResponseList);
    }

}
