package com.wealthwise.presentation.dto;

import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetStatus;
import com.wealthwise.domain.shared.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public record BudgetResponse(
        String id,
        String categoryId,
        BigDecimal limit,
        BigDecimal spent,
        BigDecimal remaining,
        double usagePercentage,
        BudgetStatus status,
        Currency currency,
        YearMonth month,
        LocalDateTime createdAt
) {
    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId().value(),
                budget.getCategoryId().value(),
                budget.getLimit().amount(),
                budget.getSpent().amount(),
                budget.getRemainingAmount().amount(),
                budget.getUsagePercentage(),
                budget.getStatus(),
                budget.getCurrency(),
                budget.getPeriod().month(),
                budget.getCreatedAt()
        );
    }
}
