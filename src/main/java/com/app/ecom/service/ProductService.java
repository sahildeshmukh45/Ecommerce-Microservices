package com.app.ecom.service;

import com.app.ecom.dto.PagedResponse;
import com.app.ecom.dto.productDtos.ProductRequest;
import com.app.ecom.dto.productDtos.ProductResponse;
import com.app.ecom.entity.Category;
import com.app.ecom.entity.Product;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.mapper.ProductMapper;
import com.app.ecom.repository.CategoryRepo;
import com.app.ecom.repository.ProductRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;

    public ProductResponse createProduct(@Valid ProductRequest productRequest) {
        // Validate and get category
        Category category = categoryRepo.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with ID " + productRequest.getCategoryId() + " not found"));

        Product product = productMapper.toEntity(productRequest);
        product.setCategory(category);

        Product savedProduct = productRepo.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse createProductWithImages(@Valid ProductRequest productRequest, List<MultipartFile> imageFiles)
            throws IOException {
        // Create product first
        ProductResponse productResponse = createProduct(productRequest);

        // Upload images if provided
        if (imageFiles != null && !imageFiles.isEmpty()) {
            Product product = productRepo.findById(productResponse.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            productImageService.uploadProductImages(product, imageFiles);

            // Return updated product response with images
            return getProduct(productResponse.getId());
        }

        return productResponse;
    }

    public ProductResponse updateProduct(Long id, @Valid ProductRequest productRequest) {
        // Find existing product
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        // Store original values for audit
        String originalName = existingProduct.getName();

        // Update category if provided
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepo.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category with ID " + productRequest.getCategoryId() + " not found"));
            existingProduct.setCategory(category);
        }

        // Use MapStruct to update other fields
        productMapper.updateProductFromRequest(productRequest, existingProduct);

        // Business validations
        validateBusinessRules(existingProduct, productRequest);

        // Save updated product
        Product updatedProduct = productRepo.save(existingProduct);

        // Log the update
        logProductUpdate(id, originalName, updatedProduct.getName());

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepo.findAll(pageable);

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
                productPage.isLast());
    }

    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        // Delete all associated images
        productImageService.deleteAllProductImages(id);

        // Delete the product
        productRepo.delete(product);
        log.info("Product deleted successfully: {}", id);
    }

    private void validateBusinessRules(Product product, ProductRequest request) {
        if (product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (product.getStockQuantity() != null && product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    private void logProductUpdate(Long productId, String originalName, String newName) {
        if (!originalName.equals(newName)) {
            log.info("Product {} name updated from '{}' to '{}'", productId, originalName, newName);
        }
    }
}