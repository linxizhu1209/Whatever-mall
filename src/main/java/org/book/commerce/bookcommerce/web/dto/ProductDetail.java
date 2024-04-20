package org.book.commerce.bookcommerce.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ProductDetail {
    private String name;
    private String description;
    private int stock;
    private long price;

    private List<ImgList> imglist;

}
