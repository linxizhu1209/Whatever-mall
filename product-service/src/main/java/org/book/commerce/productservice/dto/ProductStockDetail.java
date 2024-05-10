package org.book.commerce.productservice.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.io.Serializable;

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
