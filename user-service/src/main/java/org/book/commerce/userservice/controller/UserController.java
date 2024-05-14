package org.book.commerce.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="회원 API",description = "회원이 마이페이지를 조회하고 수정할 수 있는 API입니다")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    @Operation(summary = "마이페이지 조회",description = "회원이 마이페이지 조회를 할 수 있다")
    @GetMapping("/mypage")
    public ResponseEntity<MyPageDto> mypage(@Parameter(hidden = true) @RequestHeader("X-Authorization-Id") String userEmail){
        MyPageDto myPageDto = userService.getMypage(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(myPageDto);
    }

    @Operation(summary = "마이페이지 수정",description = "회원이 마이페이지의 정보를 수정할 수 있다")
    @PutMapping("/mypage/update")
    public ResponseEntity<String> updateMypage(@Parameter(hidden = true) @RequestHeader("X-Authorization-Id") String userEmail, @Validated @RequestBody UpdateInfo updateInfo){
        userService.updateMyPage(userEmail,updateInfo);
        return ResponseEntity.status(HttpStatus.OK).body("회원정보 수정이 완료되었습니다");
    }

}
