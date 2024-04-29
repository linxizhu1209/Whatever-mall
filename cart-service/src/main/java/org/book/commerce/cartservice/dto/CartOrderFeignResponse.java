package org.book.commerce.cartservice.dto;

public record CartOrderFeignResponse(Long productId,int count,Long cartId) {
}
