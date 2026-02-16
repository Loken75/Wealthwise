package com.wealthwise.domain.budget.event;

import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.shared.Money;

import java.time.LocalDateTime;

/**
 * Événement émis quand un budget est dépassé (> 100%).
 * Pourrait déclencher une notification à l'utilisateur.
 */
public record BudgetExceeded(
        BudgetId budgetId,
        Money limit,
        Money spent,
        LocalDateTime occurredAt
) implements DomainEvent {

    public BudgetExceeded(BudgetId budgetId, Money limit, Money spent) {
        this(budgetId, limit, spent, LocalDateTime.now());
    }
}
