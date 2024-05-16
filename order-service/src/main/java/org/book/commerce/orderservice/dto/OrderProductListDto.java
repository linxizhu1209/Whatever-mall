package org.book.commerce.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrderProductListDto {
    private int count;
    private String productName;
    private long price;
    private Long productId;

}
