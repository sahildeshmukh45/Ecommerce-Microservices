package com.app.ecom.repository;

import com.app.ecom.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    int countByProductId(@Param("productId") Long productId);

    void deleteByProductId(Long productId);

    Optional<ProductImage> findByCloudinaryPublicId(String publicId);
}