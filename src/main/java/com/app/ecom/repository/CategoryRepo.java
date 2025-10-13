package com.app.ecom.repository;

import com.app.ecom.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.name ASC")
    List<Category> findAllActiveCategories();

    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.name ASC")
    Page<Category> findAllActiveCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isActive = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY c.name ASC")
    List<Category> searchCategoriesByName(@Param("searchTerm") String searchTerm);

    boolean existsByNameIgnoreCase(String name);
}