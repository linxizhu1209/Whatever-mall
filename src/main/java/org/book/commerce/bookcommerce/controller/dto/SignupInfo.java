package org.book.commerce.bookcommerce.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupInfo {
 @NotNull
 @Email
 private String email;
 @NotNull
 private String password;
 private String address;
 @Pattern(regexp = "\\d{6}-\\d{7}$",message = "주민등록번호는 앞6자리-뒤7자리 형식으로 적어주어야합니다")
 private String registration;
 private String name;
 @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",message = "휴대폰번호는 010-xxxx-xxxx형식으로 입력해야 합니다")
 private String phonenum;

}
