package org.book.commerce.orderservice.dto;

public record ProductFeignResponse(Long productId, String name, Long price, String imgUrl, String imgName) {}
