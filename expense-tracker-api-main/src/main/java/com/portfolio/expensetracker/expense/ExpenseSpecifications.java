package com.portfolio.expensetracker.expense;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable, composable JPA {@link Specification}s for filtering expenses.
 */
public final class ExpenseSpecifications {

    private ExpenseSpecifications() {
    }

    public static Specification<Expense> ownedBy(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Expense> hasCategory(Category category) {
        if (category == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Expense> spentOnOrAfter(LocalDate from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("spentOn"), from);
    }

    public static Specification<Expense> spentOnOrBefore(LocalDate to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("spentOn"), to);
    }
}
