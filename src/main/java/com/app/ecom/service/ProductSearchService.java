package com.app.ecom.service;

import com.app.ecom.dto.PagedResponse;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.dto.productDtos.ProductSearchRequest;
import com.app.ecom.entity.Product;
import com.app.ecom.mapper.ProductMapper;
import com.app.ecom.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    public PagedResponse<ProductResponse> searchProducts(ProductSearchRequest searchRequest) {
        
        Pageable pageable = createPageable(searchRequest);
        Page<Product> productPage;

        // Choose optimal search strategy based on request
        if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().trim().isEmpty()) {
            // Use simple JPQL search for now (can be upgraded to full-text later)
            if (searchRequest.getCategoryId() != null || searchRequest.getCategoryName() != null || searchRequest.getMinPrice() != null || searchRequest.getMaxPrice() != null) {
                // Complex search with filters - use simple approach for now
                productPage = productRepo.searchProductsSimple(searchRequest.getSearchTerm().trim(), pageable);
                
                // Apply additional filters in memory (for small dataset this is acceptable)
                List<Product> filteredProducts = productPage.getContent().stream()
                    .filter(p -> searchRequest.getCategoryId() == null || 
                               (p.getCategory() != null && p.getCategory().getId().equals(searchRequest.getCategoryId())))
                    .filter(p -> searchRequest.getCategoryName() == null || 
                               (p.getCategory() != null && p.getCategory().getName().equalsIgnoreCase(searchRequest.getCategoryName())))
                    .filter(p -> searchRequest.getMinPrice() == null || 
                               (p.getPrice() != null && p.getPrice().compareTo(searchRequest.getMinPrice()) >= 0))
                    .filter(p -> searchRequest.getMaxPrice() == null || 
                               (p.getPrice() != null && p.getPrice().compareTo(searchRequest.getMaxPrice()) <= 0))
                    .collect(Collectors.toList());
                
                productPage = new org.springframework.data.domain.PageImpl<>(
                    filteredProducts, pageable, filteredProducts.size()
                );
            } else {
                // Simple text search
                productPage = productRepo.searchProductsSimple(searchRequest.getSearchTerm().trim(), pageable);
            }
            
        } else if (searchRequest.getCategoryId() != null) {
            // Category-only search by ID
            productPage = productRepo.findByCategoryId(searchRequest.getCategoryId(), pageable);
        } else if (searchRequest.getCategoryName() != null) {
            // Category-only search by name
            productPage = productRepo.findByCategoryName(searchRequest.getCategoryName(), pageable);
        } else if (searchRequest.getMinPrice() != null || searchRequest.getMaxPrice() != null) {
            // Price range search
            productPage = productRepo.findByPriceRange(
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                pageable
            );
        } else {
            // Default: get all active products
            productPage = productRepo.findAll(pageable);
        }

        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                productResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public List<String> getProductNameSuggestions(String prefix) {
        if (prefix == null || prefix.trim().length() < 2) {
            return List.of();
        }
        
        Pageable pageable = PageRequest.of(0, 10);
        return productRepo.findProductNamesByPrefix(prefix.trim(), pageable);
    }

    public List<String> getCategorySuggestions(String prefix) {
        if (prefix == null || prefix.trim().length() < 2) {
            return List.of();
        }
        
        return productRepo.findCategoriesByPrefix(prefix.trim());
    }

    private Pageable createPageable(ProductSearchRequest searchRequest) {
        Sort sort = Sort.by(
            searchRequest.getSortDirection().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            searchRequest.getSortBy()
        );
        
        return PageRequest.of(
            Math.max(0, searchRequest.getPage()),
            Math.min(100, Math.max(1, searchRequest.getSize())),
            sort
        );
    }
}