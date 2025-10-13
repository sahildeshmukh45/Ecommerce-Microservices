package com.app.ecom.controller;

import com.app.ecom.dto.productDtos.ProductImageResponse;
import com.app.ecom.entity.Product;
import com.app.ecom.service.ProductImageService;
import com.app.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/product/{productId}/images/")
@RestController
public class ProductImageController {

    private final ProductImageService productImageService;
    private final ProductService productService;

    @PostMapping("upload")
    public ResponseEntity<?> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> imageFiles) {
        
        try {
            // Verify product exists
            productService.getProduct(productId);
            
            // Create product entity for image service
            Product product = new Product();
            product.setId(productId);
            
            List<ProductImageResponse> uploadedImages = productImageService.uploadProductImages(product, imageFiles);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload images: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable Long productId) {
        List<ProductImageResponse> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("{imageId}")
    public ResponseEntity<?> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        
        try {
            productImageService.deleteProductImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("{imageId}/set-primary")
    public ResponseEntity<ProductImageResponse> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        
        ProductImageResponse response = productImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok(response);
    }
}