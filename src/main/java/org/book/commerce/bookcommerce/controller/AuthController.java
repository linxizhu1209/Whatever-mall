package org.book.commerce.bookcommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.config.security.JwtTokenProvider;
import org.book.commerce.bookcommerce.controller.dto.CommonResponseDto;
import org.book.commerce.bookcommerce.controller.dto.LoginInfo;
import org.book.commerce.bookcommerce.controller.dto.SignupInfo;
import org.book.commerce.bookcommerce.service.exception.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

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

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
            String email = authentication.getName();
            return authService.logout(email,accessToken);
        }
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그아웃에 실패했습니다!");
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshtoken(@CookieValue("refresh_token") String token){
        return authService.refresh(token);
    }

}