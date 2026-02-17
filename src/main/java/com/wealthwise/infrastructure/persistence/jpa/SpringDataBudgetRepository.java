package com.wealthwise.infrastructure.persistence.jpa;

import com.wealthwise.infrastructure.persistence.jpa.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataBudgetRepository extends JpaRepository<BudgetEntity, String> {

    Optional<BudgetEntity> findByCategoryIdAndPeriodMonth(String categoryId, String periodMonth);

    List<BudgetEntity> findByPeriodMonth(String periodMonth);
}
