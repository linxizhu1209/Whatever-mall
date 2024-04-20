package org.book.commerce.bookcommerce.web.controller;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.web.dto.MyPageDto;
import org.book.commerce.bookcommerce.web.dto.UpdateInfo;
import org.book.commerce.bookcommerce.repository.entity.CustomUserDetails;
import org.book.commerce.bookcommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    // todo 마이페이지 구현
    @GetMapping("/mypage")
    public ResponseEntity<MyPageDto> mypage(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return userService.getMypage(customUserDetails.getUsername());
    }

    @PutMapping("/mypage/update")
    public ResponseEntity updateMypage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Validated @RequestBody UpdateInfo updateInfo){
        return userService.updateMyPage(customUserDetails.getUsername(),updateInfo);
    }

}
