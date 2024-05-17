package org.book.commerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductDto {

    private String name;
    private long price;
    private int stock;
    private String description;
    private String imageUrl;
    private String imageName;
    private Boolean isLimitedEdition;
    private LocalDateTime openDateTime;
}
