package org.book.commerce.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.dto.CommonResponseDto;
import org.book.commerce.common.exception.CustomValidationException;
import org.book.commerce.userservice.dto.EmailInfo;
import org.book.commerce.userservice.dto.LoginInfo;
import org.book.commerce.userservice.dto.SignupInfo;
import org.book.commerce.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public CommonResponseDto signup(@Validated @RequestBody SignupInfo signupInfo,
                                    BindingResult bindingResult){
        log.info("회원 가입 요청이 들어왔습니다");
        if(bindingResult.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            for(FieldError error: bindingResult.getFieldErrors()){
                errors.put(error.getField(),error.getDefaultMessage());
            }
            throw new CustomValidationException("회원가입 유효성 검사에 실패하였습니다.",errors);
        }
        return authService.signup(signupInfo);
    }

    @GetMapping("/signup/email-verifications")
    public ResponseEntity<CommonResponseDto> signupConfirm(@RequestParam("key") String key){
        CommonResponseDto response = authService.registerUser(key);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/resendEmail")
    public ResponseEntity<CommonResponseDto> resendEmail(@RequestBody EmailInfo emailInfo){
        CommonResponseDto response = authService.resendEmail(emailInfo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginInfo loginInfo, HttpServletResponse httpServletResponse){
        log.info("로그인 요청이 들어왔습니다");
        return authService.login(loginInfo,httpServletResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto> logout(@RequestHeader("X-TOKEN") String token){
        CommonResponseDto response = authService.logout(token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshtoken(@CookieValue("refresh_token") String token){
        return authService.refresh(token);
    }

}