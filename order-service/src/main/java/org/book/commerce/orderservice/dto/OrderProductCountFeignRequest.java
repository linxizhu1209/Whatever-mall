package org.book.commerce.orderservice.dto;

public record OrderProductCountFeignRequest(Long productId, int count) {
}
