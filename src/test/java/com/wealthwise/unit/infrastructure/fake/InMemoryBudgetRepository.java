package com.wealthwise.unit.infrastructure.fake;

import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.model.BudgetPeriod;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.model.CategoryId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation en mémoire du BudgetRepository pour les tests.
 */
public class InMemoryBudgetRepository implements BudgetRepository {

    private final Map<String, Budget> store = new HashMap<>();

    @Override
    public Budget save(Budget budget) {
        store.put(budget.getId().value(), budget);
        return budget;
    }

    @Override
    public Optional<Budget> findById(BudgetId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public Optional<Budget> findByCategoryIdAndPeriod(CategoryId categoryId, BudgetPeriod period) {
        return store.values().stream()
                .filter(b -> b.getCategoryId().equals(categoryId))
                .filter(b -> b.getPeriod().equals(period))
                .findFirst();
    }

    @Override
    public List<Budget> findByPeriod(BudgetPeriod period) {
        return store.values().stream()
                .filter(b -> b.getPeriod().equals(period))
                .toList();
    }

    @Override
    public List<Budget> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(BudgetId id) {
        store.remove(id.value());
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
