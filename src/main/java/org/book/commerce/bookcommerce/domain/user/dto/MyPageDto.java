package org.book.commerce.bookcommerce.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MyPageDto {
    @Schema(description = "회원가입한 이메일",example = "test1234@naver.com")
    private String email;
    @Schema(description = "주소",example = "경기도 xx시 xx구 123-1 101호")
    private String address;
    @Schema(description = "핸드폰 번호",example = "010-1234-5678")
    private String phonenum;

}
