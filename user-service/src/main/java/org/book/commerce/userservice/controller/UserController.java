package org.book.commerce.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.book.commerce.userservice.dto.MyPageDto;
import org.book.commerce.userservice.dto.UpdateInfo;
import org.book.commerce.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    @GetMapping("/mypage")
    public ResponseEntity<MyPageDto> mypage(@RequestHeader("X-Authorization-Id") String userEmail){
        MyPageDto myPageDto = userService.getMypage(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(myPageDto);
    }

    @PutMapping("/mypage/update")
    public ResponseEntity<String> updateMypage(@RequestHeader("X-Authorization-Id") String userEmail, @Validated @RequestBody UpdateInfo updateInfo){
        userService.updateMyPage(userEmail,updateInfo);
        return ResponseEntity.status(HttpStatus.OK).body("회원정보 수정이 완료되었습니다");
    }
}
