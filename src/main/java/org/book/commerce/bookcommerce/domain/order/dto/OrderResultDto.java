package org.book.commerce.bookcommerce.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter

@Setter
@AllArgsConstructor
public class OrderResultDto {
    @Schema(description = "주문 고유 번호",example = "1")
    private Long orderId;
}
