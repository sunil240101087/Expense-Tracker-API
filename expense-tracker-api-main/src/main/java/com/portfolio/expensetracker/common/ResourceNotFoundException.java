package com.portfolio.expensetracker.common;

/**
 * Thrown when a requested resource cannot be found or is not owned by the caller.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
