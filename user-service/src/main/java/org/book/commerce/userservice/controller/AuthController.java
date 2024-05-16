package org.book.commerce.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.dto.CommonResponseDto;
import org.book.commerce.userservice.dto.EmailInfo;
import org.book.commerce.userservice.dto.LoginInfo;
import org.book.commerce.userservice.dto.SignupInfo;
import org.book.commerce.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public CommonResponseDto signup(@Validated @RequestBody SignupInfo signupInfo){
        log.info("회원 가입 요청이 들어왔습니다");
        return authService.signup(signupInfo);
    }

    @GetMapping("/signup/email-verifications")
    public ResponseEntity<String> signupConfirm(@RequestParam("key") String key, HttpServletResponse response){
        authService.registerUser(key);
        return ResponseEntity.status(HttpStatus.OK).body("회원인증이 성공하였습니다");
    }

    @PutMapping("/resendEmail")
    public ResponseEntity<String> resendEmail(@RequestBody EmailInfo emailInfo){
        authService.resendEmail(emailInfo);
        return ResponseEntity.status(HttpStatus.OK).body("이메일 인증 메일이 재전송되었습니다. 10분이내에 인증을 완료해주세요!");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginInfo loginInfo, HttpServletResponse httpServletResponse){
        log.info("로그인 요청이 들어왔습니다");
        return authService.login(loginInfo,httpServletResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("X-TOKEN") String token){
        authService.logout(token);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 로그아웃되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshtoken(@CookieValue("refresh_token") String token){
        return authService.refresh(token);
    }

}