package com.app.ecom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Transformation;


import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "format", "jpg",
                    "quality", "auto:good",
                    "fetch_format", "auto"
                )
            );
            
            log.info("Image uploaded successfully: {}", uploadResult.get("public_id"));
            return uploadResult;
            
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            return;
        }

        try {
            Map<String, Object> result = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "image")
            );
            
            log.info("Image deleted successfully: {}, Result: {}", publicId, result.get("result"));
            
        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    public String getOptimizedImageUrl(String publicId, int width, int height) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }

        return cloudinary.url()
                .transformation(new Transformation<>()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto:good")
                        .fetchFormat("auto"))
                .generate(publicId);
    }

}