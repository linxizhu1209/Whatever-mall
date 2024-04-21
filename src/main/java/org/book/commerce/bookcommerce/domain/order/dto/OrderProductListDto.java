package org.book.commerce.bookcommerce.domain.order.dto;

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

}
