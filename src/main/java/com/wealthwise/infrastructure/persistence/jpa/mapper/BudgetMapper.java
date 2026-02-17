package com.wealthwise.infrastructure.persistence.jpa.mapper;

import com.wealthwise.domain.budget.model.*;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.infrastructure.persistence.jpa.entity.BudgetEntity;

import java.time.YearMonth;

public final class BudgetMapper {

    private BudgetMapper() {
    }

    public static BudgetEntity toEntity(Budget budget) {
        return new BudgetEntity(
                budget.getId().value(),
                budget.getCategoryId().value(),
                budget.getLimit().amount(),
                budget.getCurrency().name(),
                budget.getSpent().amount(),
                budget.getPeriod().month().toString(),
                budget.getStatus().name(),
                budget.getCreatedAt()
        );
    }

    public static Budget toDomain(BudgetEntity entity) {
        return Budget.reconstitute(
                BudgetId.of(entity.getId()),
                CategoryId.of(entity.getCategoryId()),
                Money.of(entity.getLimitAmount().doubleValue(), Currency.valueOf(entity.getCurrency())),
                BudgetPeriod.of(YearMonth.parse(entity.getPeriodMonth())),
                Money.of(entity.getSpent().doubleValue(), Currency.valueOf(entity.getCurrency())),
                BudgetStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
