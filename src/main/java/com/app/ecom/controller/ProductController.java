package com.app.ecom.controller;

import com.app.ecom.dto.PagedResponse;
import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.dto.productDtos.ProductSearchRequest;
import com.app.ecom.service.ProductService;
import com.app.ecom.service.ProductSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/product/")
@RestController
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @PostMapping("create")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse response = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "create-with-images", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductResponse> createProductWithImages(
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestPart(value = "images", required = false) List<org.springframework.web.multipart.MultipartFile> imageFiles) {
        
        try {
            ProductResponse response = productService.createProductWithImages(productRequest, imageFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        
        ProductResponse response = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("all")
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        PagedResponse<ProductResponse> response = productService.getAllProducts(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @PostMapping("search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestBody ProductSearchRequest searchRequest) {
        
        PagedResponse<ProductResponse> searchResults = productSearchService.searchProducts(searchRequest);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProductsGet(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setSearchTerm(searchTerm);
        searchRequest.setCategoryName(category);
        
        if (minPrice != null && !minPrice.isEmpty()) {
            searchRequest.setMinPrice(new java.math.BigDecimal(minPrice));
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            searchRequest.setMaxPrice(new java.math.BigDecimal(maxPrice));
        }
        
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);
        searchRequest.setPage(page);
        searchRequest.setSize(size);

        PagedResponse<ProductResponse> searchResults = productSearchService.searchProducts(searchRequest);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("suggestions/names")
    public ResponseEntity<List<String>> getProductNameSuggestions(
            @RequestParam String prefix) {
        
        List<String> suggestions = productSearchService.getProductNameSuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("suggestions/categories")
    public ResponseEntity<List<String>> getCategorySuggestions(
            @RequestParam String prefix) {
        
        List<String> suggestions = productSearchService.getCategorySuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }

}
