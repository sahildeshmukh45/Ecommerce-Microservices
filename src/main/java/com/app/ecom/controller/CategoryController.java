package com.app.ecom.controller;

import com.app.ecom.dto.PagedResponse;
import com.app.ecom.dto.categoryDtos.CategoryRequest;
import com.app.ecom.dto.categoryDtos.CategoryResponse;
import com.app.ecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/category/")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("create")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategory(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("all")
    public ResponseEntity<PagedResponse<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        PagedResponse<CategoryResponse> response = categoryService.getAllCategories(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("active")
    public ResponseEntity<List<CategoryResponse>> getAllActiveCategories() {
        List<CategoryResponse> response = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        
        CategoryResponse response = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(
            @RequestParam String searchTerm) {
        
        List<CategoryResponse> response = categoryService.searchCategories(searchTerm);
        return ResponseEntity.ok(response);
    }
}