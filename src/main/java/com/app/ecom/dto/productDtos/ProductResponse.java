package com.app.ecom.dto.productDtos;

import com.app.ecom.dto.categoryDtos.CategoryResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private CategoryResponse category;
    private List<ProductImageResponse> images;
    private ProductImageResponse primaryImage;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
