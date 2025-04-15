package com.catalogue.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalogue.dto.CategoryDTO;
import com.catalogue.dto.CategoryItemSummaryDTO;
import com.catalogue.exception.DuplicateResourceException;
import com.catalogue.exception.ResourceNotFoundException;
import com.catalogue.model.Category;
import com.catalogue.model.CategoryItem;
import com.catalogue.repository.CategoryRepository;
import com.common.tenant.TenantContextHolder;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TenantContextHolder tenantContextHolder;

    public CategoryService(CategoryRepository categoryRepository, TenantContextHolder tenantContextHolder) {
        this.categoryRepository = categoryRepository;
        this.tenantContextHolder = tenantContextHolder;
    }

    /**
     * Get all categories for the current tenant
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        String tenantId = tenantContextHolder.getTenantId();
        return categoryRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)
                .stream()
                .map(this::convertToCategoryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a category by ID for the current tenant
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategory(Long id) {
        String tenantId = tenantContextHolder.getTenantId();
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return convertToCategoryDTO(category);
    }

    /**
     * Create a new category for the current tenant
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        String tenantId = tenantContextHolder.getTenantId();

        // Check if a category with the same name already exists for this tenant
        if (categoryRepository.existsByNameAndTenantId(categoryDTO.getName(), tenantId)) {
            throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = new Category();
        category.setTenantId(tenantId);
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setDisplayOrder(categoryDTO.getDisplayOrder());
        category.setActive(categoryDTO.getActive() == null || categoryDTO.getActive());

        Category savedCategory = categoryRepository.save(category);
        return convertToCategoryDTO(savedCategory);
    }

    /**
     * Update an existing category
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        String tenantId = tenantContextHolder.getTenantId();

        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Check for name conflicts if name is being changed
        if (!category.getName().equals(categoryDTO.getName()) &&
                categoryRepository.existsByNameAndTenantId(categoryDTO.getName(), tenantId)) {
            throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setDisplayOrder(categoryDTO.getDisplayOrder());

        if (categoryDTO.getActive() != null) {
            category.setActive(categoryDTO.getActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        return convertToCategoryDTO(updatedCategory);
    }

    /**
     * Delete a category
     */
    public void deleteCategory(Long id) {
        String tenantId = tenantContextHolder.getTenantId();

        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        categoryRepository.delete(category);
    }

    /**
     * Convert a Category entity to a CategoryDTO
     */
    private CategoryDTO convertToCategoryDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setActive(category.getActive());

        // Convert category items to DTOs
        if (category.getCategoryItems() != null) {
            List<CategoryItemSummaryDTO> itemDTOs = category.getCategoryItems().stream()
                    .map(this::convertToCategoryItemSummaryDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    /**
     * Convert a CategoryItem entity to a CategoryItemSummaryDTO
     */
    private CategoryItemSummaryDTO convertToCategoryItemSummaryDTO(CategoryItem item) {
        CategoryItemSummaryDTO dto = new CategoryItemSummaryDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setBasePrice(item.getBasePrice());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }
}
