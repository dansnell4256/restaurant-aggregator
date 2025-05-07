package com.catalogue.repository;

import com.catalogue.model.CategoryItemCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryItemCustomizationRepository extends JpaRepository<CategoryItemCustomization, Long> {

    /**
     * Find all customizations for a specific tenant
     */
    List<CategoryItemCustomization> findByTenantId(String tenantId);

    /**
     * Find all active customizations for a specific tenant
     */
    List<CategoryItemCustomization> findByTenantIdAndActiveIsTrue(String tenantId);

    /**
     * Find a customization by ID for a specific tenant
     */
    Optional<CategoryItemCustomization> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Find customizations by category item ID and tenant ID
     */
    List<CategoryItemCustomization> findByCategoryItemIdAndTenantId(Long categoryItemId, String tenantId);

    /**
     * Find active customizations by category item ID and tenant ID
     */
    List<CategoryItemCustomization> findByCategoryItemIdAndTenantIdAndActiveIsTrue(
            Long categoryItemId, String tenantId);

    /**
     * Check if a customization with the given name exists for a specific category item and tenant
     */
    boolean existsByNameAndCategoryItemIdAndTenantId(
            String name, Long categoryItemId, String tenantId);
}
