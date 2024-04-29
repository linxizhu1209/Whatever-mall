package org.book.commerce.orderservice.dto;

public record CartOrderFeignResponse(Long productId, int count, Long cartId) {
}
