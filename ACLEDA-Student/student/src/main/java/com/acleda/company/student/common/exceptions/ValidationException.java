package com.acleda.company.student.common.exceptions;

import java.util.Map;

/**
 * @author Brosna
 * @version 2.0
 * @since 04/22/2025
 */
public class ValidationException extends Exception {
    private final Map<String, Object> validationErrors;
    public ValidationException(Map<String, Object> validationErrors) {
        this.validationErrors = validationErrors;
    }

    // Getter method for retrieving validation errors
    public Map<String, Object> getValidationErrors() {
        return validationErrors;
    }
}