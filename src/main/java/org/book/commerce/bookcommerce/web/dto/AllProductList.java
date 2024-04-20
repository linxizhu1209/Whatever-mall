package org.book.commerce.bookcommerce.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AllProductList {
    private long productId;
    private String name;
    private long price;
    private String imgUrl;
    private String imgName;

}
