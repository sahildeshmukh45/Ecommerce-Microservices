package com.app.ecom.service;

import com.app.ecom.dto.PagedResponse;
import com.app.ecom.dto.categoryDtos.CategoryRequest;
import com.app.ecom.dto.categoryDtos.CategoryResponse;
import com.app.ecom.entity.Category;
import com.app.ecom.exception.ConflictException;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.mapper.CategoryMapper;
import com.app.ecom.repository.CategoryRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    public CategoryResponse createCategory(@Valid CategoryRequest categoryRequest) {
        // Check if category with same name already exists
        if (categoryRepo.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new ConflictException("Category with name '" + categoryRequest.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepo.save(category);
        
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));
        
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(
            sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy
        );
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryRepo.findAllActiveCategories(pageable);
        
        List<CategoryResponse> categoryResponses = categoryPage.getContent()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                categoryResponses,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        List<Category> categories = categoryRepo.findAllActiveCategories();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));

        // Check if another category with same name exists (excluding current category)
        if (!existingCategory.getName().equalsIgnoreCase(categoryRequest.getName()) &&
            categoryRepo.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new ConflictException("Category with name '" + categoryRequest.getName() + "' already exists");
        }

        categoryMapper.updateCategoryFromRequest(categoryRequest, existingCategory);
        Category updatedCategory = categoryRepo.save(existingCategory);
        
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));

        // Soft delete - just mark as inactive
        category.setIsActive(false);
        categoryRepo.save(category);
        
        log.info("Category soft deleted with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String searchTerm) {
        List<Category> categories = categoryRepo.searchCategoriesByName(searchTerm);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}