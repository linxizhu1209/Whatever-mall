package org.book.commerce.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartListDto {
    @Schema(description = "물품고유번호",example = "1")
    private Long productId;
    @Schema(description = "물품이름",example = "장화신은고양이")
    private String productName;
    @Schema(description = "물품가격",example = "15000")
    private Long price;
    @Schema(description = "장바구니에 담은 물품개수",example = "3")
    private int count;
}
