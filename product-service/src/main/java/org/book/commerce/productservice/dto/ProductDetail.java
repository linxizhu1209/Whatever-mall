package org.book.commerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
public class ProductDetail {
    @Schema(description = "물품이름",example = "삼체")
    private String name;
    @Schema(description = "상세설명",example = "sf과학소설이다")
    private String description;
    @Schema(description = "재고",example = "150")
    private int stock; // 재고는 구매하기 전 조그만 팝업?창에서 보여주기
    @Schema(description = "가격",example = "20000")
    private long price;

    @Schema(description = "이미지 리스트",example = "상품 소개하는 이미지 경로와 이름 리스트")
    private List<ImgList> imglist;

    @Builder.Default
    @Schema(description = "한정판인지 여부",example = "true")
    private Boolean isLimitedEdition=false;

    @Schema(description = "오픈 시간",example = "2024-05-03")
    private LocalDateTime openDateTime;

    @Schema(description = "오픈 여부",example = "true")
    @Builder.Default
    private Boolean isOpen=true;
}
