package org.book.commerce.productservice.service.mapper;

import org.book.commerce.productservice.dto.ImgList;
import org.book.commerce.productservice.domain.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImageMapper {

    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(target="imgUrl",source="imgUrl")
    @Mapping(target="imgName",source = "name")
    ImgList ImageEntityToDto(Image image);



}
