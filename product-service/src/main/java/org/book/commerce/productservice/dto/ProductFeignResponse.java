package org.book.commerce.productservice.dto;

public record ProductFeignResponse(Long productId, String name, Long price,String imgUrl,String imgName) {}
