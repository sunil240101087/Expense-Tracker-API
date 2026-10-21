package com.portfolio.expensetracker.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload for authenticating an existing account.
 */
public record LoginRequest(

        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
}
