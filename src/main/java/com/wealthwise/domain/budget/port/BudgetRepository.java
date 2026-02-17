package com.wealthwise.domain.budget.port;

import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.model.BudgetPeriod;
import com.wealthwise.domain.category.model.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des budgets.
 */
public interface BudgetRepository {

    Budget save(Budget budget);

    Optional<Budget> findById(BudgetId id);

    Optional<Budget> findByCategoryIdAndPeriod(CategoryId categoryId, BudgetPeriod period);

    List<Budget> findByPeriod(BudgetPeriod period);

    List<Budget> findAll();

    void deleteById(BudgetId id);
}
