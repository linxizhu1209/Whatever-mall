package org.book.commerce.productservice.service.mapper;


import org.book.commerce.productservice.dto.AllProductList;
import org.book.commerce.productservice.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target="productId",source="productId")
    @Mapping(target="price",source="price")
    @Mapping(target="name",source="name")
    @Mapping(target="imgUrl",source="thumbnailUrl")
    @Mapping(target = "imgName",source = "thumbnailName")
    AllProductList ProductEntityToDto(Product product);

}
