package org.book.commerce.productservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseAddProduct {
    private Long productId;
    private String message;
}
