package com.app.ecom.mapper;

import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {CategoryMapper.class, ProductImageMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "category", ignore = true) // Will be set manually in service
    @Mapping(target = "images", ignore = true)   // Will be handled separately
    Product toEntity(ProductRequest productRequest);

    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true) // Will be set manually in service
    @Mapping(target = "images", ignore = true)   // Will be handled separately
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);
}
