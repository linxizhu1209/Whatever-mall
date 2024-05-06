package org.book.commerce.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AddProductDto {
    // 유효성 추가
    @JsonProperty("name")
    @Schema(description = "물품이름",example = "장화신은고양이")
    private String name;

    @JsonProperty("price")
    @Schema(description = "가격",example = "15000")
    private long price;

    @JsonProperty("stock")
    @Schema(description = "재고",example = "1000")
    private int stock;

    @JsonProperty("description")
    @Schema(description = "상품설명",example = "어린이들이 읽기좋은 동화책입니다")
    private String description;

    @JsonProperty("imageUrl")
    @Schema(description = "대표이미지경로",example = "url~dqjwklqw.com")
    private String imageUrl;

    @JsonProperty("imageName")
    @Schema(description = "대표이미지이름",example = "장화신은 고양이")
    private String imageName;

    @JsonProperty("isLimitedEdition")
    @Schema(description = "한정판매여부", example = "true")
    private Boolean isLimitedEdition;

    @JsonProperty("openDateTime")
    @Schema(description = "오픈시간",example = "2024-05-03")
    private LocalDateTime openDateTime;
}
