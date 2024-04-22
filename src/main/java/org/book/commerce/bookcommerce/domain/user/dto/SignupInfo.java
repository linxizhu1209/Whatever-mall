package org.book.commerce.bookcommerce.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class SignupInfo {

 @Schema(description = "이메일",example = "test1234@naver.com")
 @NotNull
 @Email
 private String email;

 @Schema(description = "비밀번호",example = "영문6자이상,특수문자1개포함,숫자1개포함,총8~15자(ex: abcdef1!)")
 @NotNull
 @Pattern(regexp = "^(?=.*[A-Za-z]{6,})(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,15}$",message = "비밀번호는 영문 6자 이상, 특수문자와 숫자가 포함되어야하며, 8~15 사이여야합니다.")
 private String password;

 @Schema(description = "집 주소",example = "경기도 xx시 xx동 123-1 101호")
 private String address;

 @Schema(description = "주민등록번호",example = "123456-1234567")
 @Pattern(regexp = "\\d{6}-\\d{7}$",message = "주민등록번호는 앞6자리-뒤7자리 형식으로 적어주어야합니다")
 private String registration;

 @Schema(description = "본명",example = "홍길동")
 private String name;

 @Schema(description = "휴대폰번호",example = "010-1234-5678")
 @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",message = "휴대폰번호는 010-xxxx-xxxx형식으로 입력해야 합니다")
 private String phonenum;

}
