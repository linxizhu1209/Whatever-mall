package org.book.commerce.bookcommerce.domain.wishList.service.mapper;

import org.book.commerce.bookcommerce.domain.product.domain.Product;
import org.book.commerce.bookcommerce.domain.product.dto.AllProductList;
import org.book.commerce.bookcommerce.domain.wishList.dto.WishListDto;
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
