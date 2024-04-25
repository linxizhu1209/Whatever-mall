package org.book.commerce.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishListDto {
    @Schema(description = "물품고유번호",example = "3")
    private Long productId;
    @Schema(description = "물품이름",example = "장화신은고양잉")
    private String name;
    @Schema(description = "물품가격",example = "15000")
    private String price;
    @Schema(description = "대표이미지경로",example = "asgwqqw.com")
    private String imgUrl;
    @Schema(description = "대표이미지이름",example = "고양이")
    private String imgName;
}
