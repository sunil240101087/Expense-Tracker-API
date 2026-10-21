package com.portfolio.expensetracker.expense.dto;

import com.portfolio.expensetracker.expense.Category;
import java.math.BigDecimal;

/**
 * Aggregated spending total for a single category.
 */
public record CategoryTotal(Category category, BigDecimal total) {
}
