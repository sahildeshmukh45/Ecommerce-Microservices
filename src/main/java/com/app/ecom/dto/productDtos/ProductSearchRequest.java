package com.app.ecom.dto.productDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchRequest {
    
    private String searchTerm;
    private Long categoryId;
    private String categoryName; // For backward compatibility
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy = "name";
    private String sortDirection = "asc";
    private int page = 0;
    private int size = 20;
    
    public boolean hasFilters() {
        return searchTerm != null || categoryId != null || categoryName != null || minPrice != null || maxPrice != null;
    }
}