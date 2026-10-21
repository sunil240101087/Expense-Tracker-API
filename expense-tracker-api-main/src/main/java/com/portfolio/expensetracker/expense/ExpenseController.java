package com.portfolio.expensetracker.expense;

import com.portfolio.expensetracker.expense.dto.CategoryTotal;
import com.portfolio.expensetracker.expense.dto.ExpenseRequest;
import com.portfolio.expensetracker.expense.dto.ExpenseResponse;
import com.portfolio.expensetracker.security.AppUserDetails;
import com.portfolio.expensetracker.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Secured endpoints for managing the authenticated user's expenses.
 */
@RestController
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expenses", description = "Manage and analyze your expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @Operation(summary = "List expenses with optional filters and pagination")
    public Page<ExpenseResponse> list(
            @CurrentUser AppUserDetails user,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "spentOn") Pageable pageable) {
        return expenseService.search(user.getId(), category, from, to, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one of your expenses by id")
    public ExpenseResponse get(@CurrentUser AppUserDetails user, @PathVariable Long id) {
        return expenseService.findById(user.getId(), id);
    }

    @PostMapping
    @Operation(summary = "Create an expense")
    public ResponseEntity<ExpenseResponse> create(
            @CurrentUser AppUserDetails user,
            @Valid @RequestBody ExpenseRequest request,
            UriComponentsBuilder uriBuilder) {
        ExpenseResponse created = expenseService.create(user.getId(), request);
        URI location = uriBuilder.path("/api/v1/expenses/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update one of your expenses")
    public ExpenseResponse update(
            @CurrentUser AppUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(user.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete one of your expenses")
    public void delete(@CurrentUser AppUserDetails user, @PathVariable Long id) {
        expenseService.delete(user.getId(), id);
    }

    @GetMapping("/summary")
    @Operation(summary = "Summarize spending by category for a date range")
    public List<CategoryTotal> summary(
            @CurrentUser AppUserDetails user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return expenseService.summaryByCategory(user.getId(), from, to);
    }
}
