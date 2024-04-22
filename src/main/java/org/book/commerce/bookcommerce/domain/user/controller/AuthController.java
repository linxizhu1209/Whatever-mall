package org.book.commerce.bookcommerce.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.common.security.JwtTokenProvider;
import org.book.commerce.bookcommerce.common.dto.CommonResponseDto;
import org.book.commerce.bookcommerce.domain.user.dto.LoginInfo;
import org.book.commerce.bookcommerce.domain.user.dto.SignupInfo;
import org.book.commerce.bookcommerce.domain.user.service.AuthService;
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
@Tag(name="인증/인가 API",description = "회원이 회원가입할 수 있고 로그인/로그아웃시 인증/인가를 부여하고 무효화하는 API입니다")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입",description = "이메일 인증을 통해 회원가입할 수 있다")
    @PostMapping("/signup")
    public CommonResponseDto signup(@Valid @RequestBody SignupInfo signupInfo) throws Exception {
        log.info("회원 가입 요청이 들어왔습니다");
        return authService.signup(signupInfo);
    }

    @Operation(summary = "이메일 인증",description = "이메일 인증을 통해 회원가입을 완료한다(회원 권한 부여)")
    @GetMapping("/signup/email-verifications")
    public String signupConfirm(@RequestParam("key") String key, HttpServletResponse response) throws TimeoutException {
        authService.registerUser(key);
        return "회원인증이 성공하였습니다";
    }

    @Operation(summary = "로그인",description = "로그인이 성공하면 회원에게 토큰을 발급한다")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginInfo loginInfo, HttpServletResponse httpServletResponse) throws Exception {
        log.info("로그인 요청이 들어왔습니다");
        return authService.login(loginInfo,httpServletResponse);

    }

    @Operation(summary = "로그아웃",description = "로그아웃하면 회원에게 발급했던 토큰을 무효화한다")
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
            String token = authentication.getCredentials().toString();
            return authService.logout(token);
        }
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그아웃에 실패했습니다!");
    }

    @Operation(summary = "ACCESS토큰 재발급",description = "access토큰을 재발급한다")
    @PostMapping("/refresh")
    public ResponseEntity refreshtoken(@CookieValue("refresh_token") String token){
        return authService.refresh(token);
    }

}