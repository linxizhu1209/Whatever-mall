package org.book.commerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class EditProduct {
    @Schema(description = "이미지경로",example = "dsadqw.com")
    private String imageUrl;
    @Schema(description = "이미지이름",example = "장화신은고양이당")
    private String imageName;
    @Schema(description = "대표이미지등록여부",example = "true/false")
    private Boolean isThumbnail;
    @Schema(description = "가격",example = "14000")
    private long price;
    @Schema(description = "상품설명",example = "이 책은 남녀노소어린아이어른할것없이 좋아합니다")
    private String description;
    @Schema(description = "재고",example = "300")
    private int stock;
}
