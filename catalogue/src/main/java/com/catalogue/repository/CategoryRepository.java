package com.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalogue.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories for a specific tenant, ordered by display order
     */
    List<Category> findByTenantIdOrderByDisplayOrderAsc(String tenantId);

    /**
     * Find a category by ID for a specific tenant
     */
    Optional<Category> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Check if a category with the given name exists for a specific tenant
     */
    boolean existsByNameAndTenantId(String name, String tenantId);
}
