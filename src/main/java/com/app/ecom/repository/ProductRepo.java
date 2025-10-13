package com.app.ecom.repository;

import com.app.ecom.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    // Optimized full-text search with GIN index support
    @Query(value = """
        SELECT p.* FROM product p 
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.is_active = true 
        AND (
            to_tsvector('english', COALESCE(p.name, '') || ' ' || COALESCE(p.description, '') || ' ' || COALESCE(c.name, '')) 
            @@ plainto_tsquery('english', :searchTerm)
        )
        """, 
        countQuery = """
        SELECT COUNT(p.*) FROM product p 
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.is_active = true 
        AND (
            to_tsvector('english', COALESCE(p.name, '') || ' ' || COALESCE(p.description, '') || ' ' || COALESCE(c.name, '')) 
            @@ plainto_tsquery('english', :searchTerm)
        )
        """,
        nativeQuery = true)
    Page<Product> searchProductsFullText(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Fast category-based search with index
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.id = :categoryId ORDER BY p.name ASC")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.name = :categoryName ORDER BY p.name ASC")
    Page<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    // Price range search with compound index
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    // Combined search with multiple filters
    @Query(value = """
        SELECT p.* FROM product p 
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.is_active = true 
        AND (:categoryName IS NULL OR c.name = :categoryName)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:searchTerm IS NULL OR 
            to_tsvector('english', COALESCE(p.name, '') || ' ' || COALESCE(p.description, '') || ' ' || COALESCE(c.name, '')) 
            @@ plainto_tsquery('english', :searchTerm))
        """, 
        countQuery = """
        SELECT COUNT(p.*) FROM product p 
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.is_active = true 
        AND (:categoryName IS NULL OR c.name = :categoryName)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:searchTerm IS NULL OR 
            to_tsvector('english', COALESCE(p.name, '') || ' ' || COALESCE(p.description, '') || ' ' || COALESCE(c.name, '')) 
            @@ plainto_tsquery('english', :searchTerm))
        """,
        nativeQuery = true)
    Page<Product> searchWithFilters(
        @Param("searchTerm") String searchTerm,
        @Param("categoryName") String categoryName,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    // Simple text search using JPQL (fallback for complex queries)
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.name ASC")
    Page<Product> searchProductsSimple(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Fast name-based search for autocomplete
    @Query("SELECT DISTINCT p.name FROM Product p WHERE p.isActive = true AND LOWER(p.name) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY p.name ASC")
    List<String> findProductNamesByPrefix(@Param("prefix") String prefix, Pageable pageable);

    // Category suggestions for autocomplete
    @Query("SELECT DISTINCT p.category.name FROM Product p WHERE p.isActive = true AND LOWER(p.category.name) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY p.category.name ASC")
    List<String> findCategoriesByPrefix(@Param("prefix") String prefix);


}
