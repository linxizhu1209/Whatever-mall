package org.book.commerce.productservice.dto;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDetail {
    private Long productId;
    private String productName;
    private int stock;
    private Boolean modified;
}
