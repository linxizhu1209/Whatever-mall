package org.book.commerce.bookcommerce.domain.wishList.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="위시리스트 API",description = "마음에 드는 물건을 저장해둘 수 있는 위시리스트 API입니다")
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;
    @Operation(summary = "물품 찜하기",description = "맘에드는 물건을 위시리스트에 추가한다(이미 위시리스트에 있는 물품은 추가 불가)")
    @PostMapping("/add/{productId}")
    public ResponseEntity addWish(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long productId){
        log.info("[WishController] 찜 목록 추가 요청이 들어왔습니다");
        return wishService.addWish(customUserDetails,productId);
    }

    @Operation(summary = "위시리스트 물품 삭제",description = "위시리스트에 있는 물품을 삭제한다")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity deleteWish(@PathVariable Long productId){
        log.info("[WishController] 찜 목록 삭제 요청이 들어왔습니다");
        return wishService.deleteWish(productId);
    }
    @Operation(summary = "위시리스트 조회",description = "위시리스트를 조회한다")
    @GetMapping("/list")
    public ResponseEntity<List<WishListDto>> getWishList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("[WishController] 찜 목록 조회 요청이 들어왔습니다");
        return wishService.getWishList(customUserDetails);
    }

}
