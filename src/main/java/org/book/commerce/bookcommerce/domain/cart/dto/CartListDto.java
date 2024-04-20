package org.book.commerce.bookcommerce.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartListDto {
    private Long productId;
    private String productName;
    private Long price;
    private int count;

}
