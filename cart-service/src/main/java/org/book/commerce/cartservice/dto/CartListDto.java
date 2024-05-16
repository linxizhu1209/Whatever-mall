package org.book.commerce.cartservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartListDto {
    private Long productId;
    private String productName;
    private Long price;
    private int count;
}
