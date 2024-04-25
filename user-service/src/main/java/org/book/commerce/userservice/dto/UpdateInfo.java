package org.book.commerce.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class UpdateInfo {
    @Schema(description = "주소",example = "경기도 xx시 xx구 123-12 101호")
    private String address;

    @Schema(description = "휴대폰 번호",example = "010-1234-5678")
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",message = "휴대폰번호는 010-xxxx-xxxx형식으로 입력해야 합니다")
    private String phoneNum;

    @Schema(description = "패스워드",example = "영문6자이상,특수문자1개포함,숫자1개포함,총8~15자")
    @Pattern(regexp = "^(?=.*[A-Za-z]{6,})(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,15}$",message = "비밀번호는 영문 6자 이상, 특수문자와 숫자가 포함되어야하며, 8~15 사이여야합니다.")
    private String password;
}
