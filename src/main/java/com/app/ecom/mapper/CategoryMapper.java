package com.app.ecom.mapper;

import com.app.ecom.dto.categoryDtos.CategoryRequest;
import com.app.ecom.dto.categoryDtos.CategoryResponse;
import com.app.ecom.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category toEntity(CategoryRequest categoryRequest);

    @Mapping(target = "productCount", expression = "java(category.getProducts() != null ? (long) category.getProducts().size() : 0L)")
    CategoryResponse toResponse(Category category);

    void updateCategoryFromRequest(CategoryRequest categoryRequest, @MappingTarget Category category);
}