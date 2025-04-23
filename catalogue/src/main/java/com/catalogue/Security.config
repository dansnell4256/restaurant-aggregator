package com.catalogue.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalogue.dto.ApiResponse;
import com.catalogue.dto.CategoryDTO;
import com.catalogue.service.CategoryService;
import com.catalogue.util.ValidationUtils;
import com.common.tenant.TenantContextHolder;

/**
 * Controller for managing restaurant categories.
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/catalogue")
public class CategoryController {

    private final CategoryService categoryService;
    private final TenantContextHolder tenantContextHolder;

    /**
     * Constructor for CategoryController.
     *
     * @param categoryService Service for category operations
     * @param tenantContextHolder Utility for managing tenant context
     */
    public CategoryController(CategoryService categoryService, TenantContextHolder tenantContextHolder) {
        this.categoryService = categoryService;
        this.tenantContextHolder = tenantContextHolder;
    }

    /**
     * Get all categories for the specified tenant.
     *
     * @param tenantId The tenant identifier
     * @return ApiResponse containing the list of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories(@PathVariable String tenantId) {
        // Set the tenant context for this request
        tenantContextHolder.setTenantId(tenantId);

        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            ApiResponse<List<CategoryDTO>> response = ApiResponse.success(
                    "Categories retrieved successfully", categories);
            return ResponseEntity.ok(response);
        } finally {
            // Always clear the tenant context after the request
            tenantContextHolder.clear();
        }
    }

    /**
     * Create a new category for the specified tenant.
     *
     * @param tenantId The tenant identifier
     * @param categoryDTO The category data to create
     * @return ApiResponse containing the created category
     */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @PathVariable String tenantId,
            @RequestBody CategoryDTO categoryDTO) {

        // Validate the input data
        ValidationUtils.validateCategoryDTO(categoryDTO);

        // Set the tenant context for this request
        tenantContextHolder.setTenantId(tenantId);

        try {
            CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            ApiResponse<CategoryDTO> response = ApiResponse.success(
                    "Category created successfully", createdCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } finally {
            // Always clear the tenant context after the request
            tenantContextHolder.clear();
        }
    }
}
