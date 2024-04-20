package org.book.commerce.bookcommerce.domain.product.dto;

import lombok.Getter;

@Getter
public class AddProductDto {
    // 유효성 추가
    private String name;
    private long price;
    private int stock;
    private String description;
    private String imageUrl;
    private String imageName;
}
