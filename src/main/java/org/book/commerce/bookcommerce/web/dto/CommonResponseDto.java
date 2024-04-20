package org.book.commerce.bookcommerce.web.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommonResponseDto {
    private int statusCode;
    private boolean success;
    private String message;
}
