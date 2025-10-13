package com.app.ecom.mapper;

import com.app.ecom.dto.productDtos.ProductImageResponse;
import com.app.ecom.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    ProductImageResponse toResponse(ProductImage productImage);
}