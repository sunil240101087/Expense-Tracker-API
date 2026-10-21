package com.portfolio.expensetracker.expense;

import com.portfolio.expensetracker.expense.dto.CategoryTotal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Data-access layer for {@link Expense} entities.
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support dynamic, type-safe
 * filtering used by the search endpoint.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Expense> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("""
            select new com.portfolio.expensetracker.expense.dto.CategoryTotal(e.category, sum(e.amount))
            from Expense e
            where e.owner.id = :ownerId
              and e.spentOn between :from and :to
            group by e.category
            order by sum(e.amount) desc
            """)
    List<CategoryTotal> totalsByCategory(@Param("ownerId") Long ownerId,
                                         @Param("from") LocalDate from,
                                         @Param("to") LocalDate to);
}
