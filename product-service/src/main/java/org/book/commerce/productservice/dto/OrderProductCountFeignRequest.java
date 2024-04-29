package org.book.commerce.productservice.dto;

public record OrderProductCountFeignRequest(Long productId, int count) {
}
