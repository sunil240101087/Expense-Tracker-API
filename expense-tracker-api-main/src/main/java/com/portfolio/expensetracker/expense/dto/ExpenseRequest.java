package com.portfolio.expensetracker.expense.dto;

import com.portfolio.expensetracker.expense.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload for creating or updating an expense.
 */
public record ExpenseRequest(

        @NotBlank(message = "description must not be blank")
        @Size(max = 255, message = "description must be at most 255 characters")
        String description,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "category is required")
        Category category,

        @NotNull(message = "spentOn date is required")
        @PastOrPresent(message = "spentOn cannot be in the future")
        LocalDate spentOn
) {
}
