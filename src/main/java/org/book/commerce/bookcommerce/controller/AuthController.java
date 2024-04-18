package org.book.commerce.bookcommerce.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.controller.dto.CommonResponseDto;
import org.book.commerce.bookcommerce.controller.dto.LoginInfo;
import org.book.commerce.bookcommerce.controller.dto.SignupInfo;
import org.book.commerce.bookcommerce.service.exception.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public CommonResponseDto signup(@Valid @RequestBody SignupInfo signupInfo) throws Exception {
        log.info("회원 가입 요청이 들어왔습니다");
        return authService.signup(signupInfo);
    }

    @GetMapping("/signup/email-verifications")
    public String signupConfirm(@RequestParam("key") String key, HttpServletResponse response) throws TimeoutException {
        authService.registerUser(key);
        return "회원인증이 성공하였습니다"; // 이후에 redirect 페이지로 변경 예정
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginInfo loginInfo, HttpServletResponse httpServletResponse) throws Exception {
        log.info("로그인 요청이 들어왔습니다");
        return authService.login(loginInfo,httpServletResponse);

    }


}