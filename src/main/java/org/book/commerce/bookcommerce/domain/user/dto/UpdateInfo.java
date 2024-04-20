package org.book.commerce.bookcommerce.domain.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
public class UpdateInfo {
    private String address;
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",message = "휴대폰번호는 010-xxxx-xxxx형식으로 입력해야 합니다")
    private String phoneNum;
    @Pattern(regexp = "^(?=.*[A-Za-z]{6,})(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,15}$",message = "비밀번호는 영문 6자 이상, 특수문자와 숫자가 포함되어야하며, 8~15 사이여야합니다.")
    private String password;
}
