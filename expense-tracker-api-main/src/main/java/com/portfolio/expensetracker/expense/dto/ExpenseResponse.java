package com.portfolio.expensetracker.expense.dto;

import com.portfolio.expensetracker.expense.Category;
import com.portfolio.expensetracker.expense.Expense;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * API representation of an expense returned to clients.
 */
public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        Category category,
        LocalDate spentOn,
        Instant createdAt,
        Instant updatedAt
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getSpentOn(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
