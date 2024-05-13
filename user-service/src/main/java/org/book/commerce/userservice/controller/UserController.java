package org.book.commerce.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.book.commerce.userservice.dto.MyPageDto;
import org.book.commerce.userservice.dto.UpdateInfo;
import org.book.commerce.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name="회원 API",description = "회원이 마이페이지를 조회하고 수정할 수 있는 API입니다")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    // todo 마이페이지 구현
    @Operation(summary = "마이페이지 조회",description = "회원이 마이페이지 조회를 할 수 있다")
    @GetMapping("/mypage")
    public ResponseEntity<MyPageDto> mypage(@Parameter(hidden = true) @RequestHeader("X-Authorization-Id") String userEmail){
        return userService.getMypage(userEmail);
    }

    @Operation(summary = "마이페이지 수정",description = "회원이 마이페이지의 정보를 수정할 수 있다")
    @PutMapping("/mypage/update")
    public ResponseEntity updateMypage(@Parameter(hidden = true) @RequestHeader("X-Authorization-Id") String userEmail, @Validated @RequestBody UpdateInfo updateInfo){
        return userService.updateMyPage(userEmail,updateInfo);
    }

}
