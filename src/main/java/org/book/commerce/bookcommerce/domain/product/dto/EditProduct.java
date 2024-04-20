package org.book.commerce.bookcommerce.domain.product.dto;

import lombok.Getter;

@Getter
public class EditProduct {
    private String imageUrl;
    private String imageName;
    private Boolean isThumbnail;
    private long price;
    private String description;
}
