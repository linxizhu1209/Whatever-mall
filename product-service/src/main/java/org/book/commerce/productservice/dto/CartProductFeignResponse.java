package org.book.commerce.productservice.dto;

public record CartProductFeignResponse(Long productId, String name, long price){}