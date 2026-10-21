package com.portfolio.expensetracker.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload for registering a new account.
 */
public record RegisterRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
        String password,

        @NotBlank(message = "displayName is required")
        @Size(max = 100, message = "displayName must be at most 100 characters")
        String displayName
) {
}
