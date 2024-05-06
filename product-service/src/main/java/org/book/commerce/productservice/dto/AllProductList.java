package org.book.commerce.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class AllProductList {

    @Schema(description = "제품고유번호",example = "1")
    private long productId;
    @Schema(description = "제품이름",example = "장화신은고양이")
    private String name;
    @Schema(description = "가격",example = "15000")
    private long price;
    @Schema(description = "대표이미지경로",example = "dwdjlqwd~.com")
    private String imgUrl;
    @Schema(description = "대표이미지이름",example = "장화신은고양이다")
    private String imgName;


}
