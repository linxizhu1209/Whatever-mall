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
 @Pattern(regexp = "^(?=.*[A-Za-z]{6,})(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,15}$",message = "비밀번호는 영문 6자 이상, 특수문자와 숫자가 포함되어야하며, 8~15 사이여야합니다.")
 private String password;
 private String address;
 @Pattern(regexp = "\\d{6}-\\d{7}$",message = "주민등록번호는 앞6자리-뒤7자리 형식으로 적어주어야합니다")
 private String registration;
 private String name;
 @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",message = "휴대폰번호는 010-xxxx-xxxx형식으로 입력해야 합니다")
 private String phonenum;

}
