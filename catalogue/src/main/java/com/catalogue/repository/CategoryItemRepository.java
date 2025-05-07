package com.catalogue.repository;

import com.catalogue.model.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {

    /**
     * Find all category items for a specific tenant, ordered by display order
     */
    List<CategoryItem> findByTenantIdOrderByDisplayOrderAsc(String tenantId);

    /**
     * Find all active category items for a specific tenant, ordered by display order
     */
    List<CategoryItem> findByTenantIdAndActiveIsTrueOrderByDisplayOrderAsc(String tenantId);

    /**
     * Find a category item by ID for a specific tenant
     */
    Optional<CategoryItem> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Find category items by category ID and tenant ID
     */
    List<CategoryItem> findByCategoryIdAndTenantId(Long categoryId, String tenantId);

    /**
     * Find active category items by category ID and tenant ID
     */
    List<CategoryItem> findByCategoryIdAndTenantIdAndActiveIsTrue(Long categoryId, String tenantId);

    /**
     * Check if a category item with the given name exists for a specific tenant
     */
    boolean existsByNameAndTenantId(String name, String tenantId);

    /**
     * Check if a category item with the given SKU exists for a specific tenant
     */
    boolean existsBySkuAndTenantId(String sku, String tenantId);
}
