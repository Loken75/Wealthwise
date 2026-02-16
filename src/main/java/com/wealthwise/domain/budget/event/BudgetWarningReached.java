package com.wealthwise.domain.budget.event;

import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.budget.model.BudgetId;

import java.time.LocalDateTime;

/**
 * Événement émis quand un budget atteint le seuil d'avertissement (80%).
 * Pourrait déclencher une notification préventive.
 */
public record BudgetWarningReached(
        BudgetId budgetId,
        double percentage,
        LocalDateTime occurredAt
) implements DomainEvent {

    public BudgetWarningReached(BudgetId budgetId, double percentage) {
        this(budgetId, percentage, LocalDateTime.now());
    }
}
