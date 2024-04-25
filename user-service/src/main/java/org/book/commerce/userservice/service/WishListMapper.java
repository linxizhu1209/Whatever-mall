package org.book.commerce.userservice.service;

import org.book.commerce.productservice.domain.Product;
import org.book.commerce.userservice.dto.WishListDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WishListMapper {
    WishListMapper INSTANCE = Mappers.getMapper(WishListMapper.class);

    @Mapping(target="productId",source="productId")
    @Mapping(target="price",source="price")
    @Mapping(target="name",source="name")
    @Mapping(target="imgUrl",source="thumbnailUrl")
    @Mapping(target = "imgName",source = "thumbnailName")
    WishListDto ProductEntityToWishListDto(Product product);

}
