package org.book.commerce.bookcommerce.domain.cart.service.mapper;

import org.book.commerce.bookcommerce.domain.cart.domain.Cart;
import org.book.commerce.bookcommerce.domain.cart.dto.CartListDto;
import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);
    @Mapping(source="productId",target = "productId")
    @Mapping(source="name",target="productName")
    @Mapping(source="price",target="price")
    CartListDto CartEntityToDto(Product product);

}
