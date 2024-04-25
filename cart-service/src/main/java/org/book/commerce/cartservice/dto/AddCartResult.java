package org.book.commerce.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddCartResult {
    @Schema(description = "장바구니 고유번호",example = "1")
    private Long cartId;
}
