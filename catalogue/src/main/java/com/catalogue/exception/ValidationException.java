package com.catalogue.exception;

import java.util.List;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    /**
     * Constructor with message and validation errors.
     *
     * @param message Error message
     * @param errors List of validation errors
     */
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Get validation errors.
     *
     * @return List of validation error messages
     */
    public List<String> getErrors() {
        return errors;
    }
}
