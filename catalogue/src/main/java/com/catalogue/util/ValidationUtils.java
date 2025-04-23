package com.catalogue.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.catalogue.dto.CategoryDTO;
import com.catalogue.exception.ValidationException;

/**
 * Utility class for validating API input.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates a Category DTO.
     *
     * @param categoryDTO The category DTO to validate
     * @throws ValidationException If validation fails
     */
    public static void validateCategoryDTO(CategoryDTO categoryDTO) {
        List<String> errors = new ArrayList<>();

        // Name validation
        if (categoryDTO.getName() == null || StringUtils.isBlank(categoryDTO.getName())) {
            errors.add("Category name is required");
        } else if (categoryDTO.getName().length() > 100) {
            errors.add("Category name cannot exceed 100 characters");
        }

        // Description validation
        if (categoryDTO.getDescription() != null && categoryDTO.getDescription().length() > 500) {
            errors.add("Category description cannot exceed 500 characters");
        }

        // Display order validation
        if (categoryDTO.getDisplayOrder() != null && categoryDTO.getDisplayOrder() < 0) {
            errors.add("Display order must be a non-negative number");
        }

        // Throw exception if there are any validation errors
        if (!errors.isEmpty()) {
            throw new ValidationException("Category validation failed", errors);
        }
    }
}
