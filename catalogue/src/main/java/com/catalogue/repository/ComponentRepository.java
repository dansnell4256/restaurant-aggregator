package com.catalogue.repository;

import com.catalogue.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

    /**
     * Find all components for a specific tenant
     */
    List<Component> findByTenantId(String tenantId);

    /**
     * Find a component by ID for a specific tenant
     */
    Optional<Component> findByIdAndTenantId(Long id, String tenantId);

    /**
     * Find components by their IDs and tenant ID
     */
    List<Component> findByIdInAndTenantId(Set<Long> ids, String tenantId);

    /**
     * Check if a component with the given name exists for a specific tenant
     */
    boolean existsByNameAndTenantId(String name, String tenantId);

    /**
     * Find all allergenic components for a specific tenant
     */
    List<Component> findByIsAllergenicIsTrueAndTenantId(String tenantId);

    /**
     * Find components by category item ID and tenant ID
     */
    @Query("SELECT c FROM Component c JOIN c.categoryItems ci " +
            "WHERE ci.id = :categoryItemId AND c.tenantId = :tenantId")
    List<Component> findByCategoryItemIdAndTenantId(
            @Param("categoryItemId") Long categoryItemId,
            @Param("tenantId") String tenantId);

    /**
     * Find components by customization ID and tenant ID
     */
    @Query("SELECT c FROM Component c JOIN c.customizations cust " +
            "WHERE cust.id = :customizationId AND c.tenantId = :tenantId")
    List<Component> findByCustomizationIdAndTenantId(
            @Param("customizationId") Long customizationId,
            @Param("tenantId") String tenantId);
}
