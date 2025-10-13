package com.app.ecom.service;

import com.app.ecom.dto.productDtos.ProductImageResponse;
import com.app.ecom.entity.Product;
import com.app.ecom.entity.ProductImage;
import com.app.ecom.exception.ResourceNotFoundException;
import com.app.ecom.mapper.ProductImageMapper;
import com.app.ecom.repository.ProductImageRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageService {

    private final ProductImageRepo productImageRepo;
    private final ProductImageMapper productImageMapper;
    private final CloudinaryService cloudinaryService;

    private static final int MAX_IMAGES_PER_PRODUCT = 6;
    private static final String PRODUCT_IMAGES_FOLDER = "ecom/products";

    public List<ProductImageResponse> uploadProductImages(Product product, List<MultipartFile> imageFiles)
            throws IOException {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return List.of();
        }

        // Check current image count
        int currentImageCount = productImageRepo.countByProductId(product.getId());
        int newImagesCount = imageFiles.size();

        if (currentImageCount + newImagesCount > MAX_IMAGES_PER_PRODUCT) {
            throw new IllegalArgumentException(
                    String.format("Cannot upload %d images. Product already has %d images. Maximum allowed is %d.",
                            newImagesCount, currentImageCount, MAX_IMAGES_PER_PRODUCT));
        }

        List<ProductImage> uploadedImages = new java.util.ArrayList<>();
        int displayOrder = currentImageCount + 1;
        boolean isFirstImage = currentImageCount == 0;

        for (MultipartFile file : imageFiles) {
            if (file != null && !file.isEmpty()) {
                try {
                    Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, PRODUCT_IMAGES_FOLDER);

                    ProductImage productImage = new ProductImage(
                            uploadResult.get("secure_url").toString(),
                            uploadResult.get("public_id").toString(),
                            displayOrder++,
                            product);

                    // Set first image as primary if no primary image exists
                    if (isFirstImage && uploadedImages.isEmpty()) {
                        productImage.setIsPrimary(true);
                    }

                    ProductImage savedImage = productImageRepo.save(productImage);
                    uploadedImages.add(savedImage);

                } catch (IOException e) {
                    log.error("Failed to upload image for product {}: {}", product.getId(), e.getMessage());
                    // Clean up any successfully uploaded images
                    cleanupUploadedImages(uploadedImages);
                    throw e;
                }
            }
        }

        log.info("Successfully uploaded {} images for product {}", uploadedImages.size(), product.getId());

        return uploadedImages.stream()
                .map(productImageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {
        List<ProductImage> images = productImageRepo.findByProductIdOrderByDisplayOrderAsc(productId);
        return images.stream()
                .map(productImageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteProductImage(Long imageId) throws IOException {
        ProductImage productImage = productImageRepo.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image with ID " + imageId + " not found"));

        // Delete from Cloudinary
        try {
            cloudinaryService.deleteImage(productImage.getCloudinaryPublicId());
        } catch (IOException e) {
            log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
            // Continue with database deletion even if Cloudinary deletion fails
        }

        // If this was the primary image, set another image as primary
        if (productImage.getIsPrimary()) {
            List<ProductImage> otherImages = productImageRepo.findByProductIdOrderByDisplayOrderAsc(
                    productImage.getProduct().getId());

            otherImages.stream()
                    .filter(img -> !img.getId().equals(imageId))
                    .findFirst()
                    .ifPresent(img -> {
                        img.setIsPrimary(true);
                        productImageRepo.save(img);
                    });
        }

        productImageRepo.delete(productImage);
        log.info("Product image deleted successfully: {}", imageId);
    }

    public void deleteAllProductImages(Long productId) {
        List<ProductImage> images = productImageRepo.findByProductIdOrderByDisplayOrderAsc(productId);

        for (ProductImage image : images) {
            try {
                cloudinaryService.deleteImage(image.getCloudinaryPublicId());
            } catch (IOException e) {
                log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
            }
        }

        productImageRepo.deleteByProductId(productId);
        log.info("All images deleted for product: {}", productId);
    }

    public ProductImageResponse setPrimaryImage(Long imageId) {
        ProductImage productImage = productImageRepo.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image with ID " + imageId + " not found"));

        // Remove primary flag from other images of the same product
        List<ProductImage> productImages = productImageRepo.findByProductIdOrderByDisplayOrderAsc(
                productImage.getProduct().getId());

        productImages.forEach(img -> {
            img.setIsPrimary(img.getId().equals(imageId));
            productImageRepo.save(img);
        });

        log.info("Primary image set for product {}: {}", productImage.getProduct().getId(), imageId);
        return productImageMapper.toResponse(productImage);
    }

    private void cleanupUploadedImages(List<ProductImage> uploadedImages) {
        for (ProductImage image : uploadedImages) {
            try {
                cloudinaryService.deleteImage(image.getCloudinaryPublicId());
                productImageRepo.delete(image);
            } catch (Exception e) {
                log.error("Failed to cleanup uploaded image: {}", e.getMessage());
            }
        }
    }
}