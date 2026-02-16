package com.wealthwise.domain.budget.model;

import java.util.UUID;

/**
 * Identifiant unique d'un budget.
 */
public record BudgetId(String value) {

    public BudgetId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BudgetId must not be null or blank");
        }
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID().toString());
    }

    public static BudgetId of(String value) {
        return new BudgetId(value);
    }
}
