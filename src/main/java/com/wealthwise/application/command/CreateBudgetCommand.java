package com.wealthwise.application.command;

import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;

import java.time.YearMonth;

/**
 * Commande pour créer un nouveau budget mensuel.
 */
public record CreateBudgetCommand(
        CategoryId categoryId,
        double limitAmount,
        Currency currency,
        YearMonth month
) {
}
