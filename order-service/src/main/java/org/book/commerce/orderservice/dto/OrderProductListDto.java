package org.book.commerce.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrderProductListDto {
    @Schema(description = "주문한 물품 개수",example = "3")
    private int count;
    @Schema(description = "물품이름",example = "장화신은고양이")
    private String productName;
    @Schema(description = "물품가격",example = "15000")
    private long price;

}
