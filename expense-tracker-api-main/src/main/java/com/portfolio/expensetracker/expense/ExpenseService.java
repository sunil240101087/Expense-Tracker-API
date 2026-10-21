package com.portfolio.expensetracker.expense;

import com.portfolio.expensetracker.common.ResourceNotFoundException;
import com.portfolio.expensetracker.expense.dto.CategoryTotal;
import com.portfolio.expensetracker.expense.dto.ExpenseRequest;
import com.portfolio.expensetracker.expense.dto.ExpenseResponse;
import com.portfolio.expensetracker.user.User;
import com.portfolio.expensetracker.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for managing a user's expenses. All operations are scoped to
 * the owning user so callers can never read or mutate another user's data.
 */
@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> search(Long ownerId,
                                        Category category,
                                        LocalDate from,
                                        LocalDate to,
                                        Pageable pageable) {
        Specification<Expense> spec = Specification.where(ExpenseSpecifications.ownedBy(ownerId))
                .and(ExpenseSpecifications.hasCategory(category))
                .and(ExpenseSpecifications.spentOnOrAfter(from))
                .and(ExpenseSpecifications.spentOnOrBefore(to));
        return expenseRepository.findAll(spec, pageable).map(ExpenseResponse::from);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long ownerId, Long expenseId) {
        return expenseRepository.findByIdAndOwnerId(expenseId, ownerId)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> notFound(expenseId));
    }

    public ExpenseResponse create(Long ownerId, ExpenseRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerId));
        Expense expense = new Expense(
                owner,
                request.description(),
                request.amount(),
                request.category(),
                request.spentOn());
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public ExpenseResponse update(Long ownerId, Long expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findByIdAndOwnerId(expenseId, ownerId)
                .orElseThrow(() -> notFound(expenseId));
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setSpentOn(request.spentOn());
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public void delete(Long ownerId, Long expenseId) {
        Expense expense = expenseRepository.findByIdAndOwnerId(expenseId, ownerId)
                .orElseThrow(() -> notFound(expenseId));
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public List<CategoryTotal> summaryByCategory(Long ownerId, LocalDate from, LocalDate to) {
        LocalDate effectiveFrom = (from != null) ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate effectiveTo = (to != null) ? to : LocalDate.now();
        return expenseRepository.totalsByCategory(ownerId, effectiveFrom, effectiveTo);
    }

    private ResourceNotFoundException notFound(Long expenseId) {
        return new ResourceNotFoundException("Expense not found: " + expenseId);
    }
}
