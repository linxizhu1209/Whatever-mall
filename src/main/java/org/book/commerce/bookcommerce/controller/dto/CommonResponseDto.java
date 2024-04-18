package org.book.commerce.bookcommerce.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommonResponseDto {
    private int statusCode;
    private boolean success;
    private String message;
}
