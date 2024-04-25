package org.book.commerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AddProductDto {
    // 유효성 추가
    @Schema(description = "물품이름",example = "장화신은고양이")
    private String name;

    @Schema(description = "가격",example = "15000")
    private long price;

    @Schema(description = "재고",example = "1000")
    private int stock;

    @Schema(description = "상품설명",example = "어린이들이 읽기좋은 동화책입니다")
    private String description;

    @Schema(description = "대표이미지경로",example = "url~dqjwklqw.com")
    private String imageUrl;

    @Schema(description = "대표이미지이름",example = "장화신은 고양이")
    private String imageName;
}
