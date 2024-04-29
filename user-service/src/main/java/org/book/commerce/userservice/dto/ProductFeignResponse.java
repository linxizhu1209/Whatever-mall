package org.book.commerce.userservice.dto;

public record ProductFeignResponse(Long productId, String name,
                                   Long price,String imgUrl,String imgName) {
}
