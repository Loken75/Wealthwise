package com.wealthwise.application.command;

import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import com.wealthwise.domain.transaction.model.TransactionId;

/**
 * Commande pour catégoriser une transaction existante.
 */
public record CategorizeTransactionCommand(
        TransactionId transactionId,
        CategoryId categoryId,
        ConfidenceLevel confidenceLevel
) {
}
