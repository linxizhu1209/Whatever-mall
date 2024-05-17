package org.book.commerce.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishListDto {
    private Long productId;
    private String name;
    private String price;
    private String imgUrl;
    private String imgName;
}
