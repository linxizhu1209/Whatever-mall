package org.book.commerce.productservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @Builder.Default
    private Boolean isLimitedEdition=false;
    private LocalDateTime openDateTime;
    @Builder.Default
    private Boolean isOpen=true;
}
