package org.book.commerce.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {

    @Schema(description = "회원가입한 이메일",example = "test1234")
    private String email;
    @Schema(description = "회원가입시 사용한 패스워드",example = "abcdef1!")
    private String password;

}
