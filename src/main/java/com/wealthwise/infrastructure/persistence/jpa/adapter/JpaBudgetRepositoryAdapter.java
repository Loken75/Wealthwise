package com.wealthwise.infrastructure.persistence.jpa.adapter;

import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.model.BudgetPeriod;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.infrastructure.persistence.jpa.SpringDataBudgetRepository;
import com.wealthwise.infrastructure.persistence.jpa.mapper.BudgetMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBudgetRepositoryAdapter implements BudgetRepository {

    private final SpringDataBudgetRepository jpaRepository;

    public JpaBudgetRepositoryAdapter(SpringDataBudgetRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Budget save(Budget budget) {
        jpaRepository.save(BudgetMapper.toEntity(budget));
        return budget;
    }

    @Override
    public Optional<Budget> findById(BudgetId id) {
        return jpaRepository.findById(id.value())
                .map(BudgetMapper::toDomain);
    }

    @Override
    public Optional<Budget> findByCategoryIdAndPeriod(CategoryId categoryId, BudgetPeriod period) {
        return jpaRepository.findByCategoryIdAndPeriodMonth(
                        categoryId.value(), period.month().toString())
                .map(BudgetMapper::toDomain);
    }

    @Override
    public List<Budget> findByPeriod(BudgetPeriod period) {
        return jpaRepository.findByPeriodMonth(period.month().toString()).stream()
                .map(BudgetMapper::toDomain)
                .toList();
    }

    @Override
    public List<Budget> findAll() {
        return jpaRepository.findAll().stream()
                .map(BudgetMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(BudgetId id) {
        jpaRepository.deleteById(id.value());
    }
}
