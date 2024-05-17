package org.book.commerce.productservice.dto;

import lombok.Getter;

@Getter
public class EditProduct {
    private String imageUrl;
    private String imageName;
    private Boolean isThumbnail;
    private long price;
    private String description;
    private int stock;
}
