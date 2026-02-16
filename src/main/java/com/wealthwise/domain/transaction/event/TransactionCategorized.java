package com.wealthwise.domain.transaction.event;

import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import com.wealthwise.domain.transaction.model.TransactionId;

import java.time.LocalDateTime;

/**
 * Événement émis quand une transaction est catégorisée
 * (automatiquement ou manuellement).
 *
 * Le contexte Budget écoute cet événement pour comptabiliser
 * la dépense dans le bon budget de catégorie.
 */
public record TransactionCategorized(
        TransactionId transactionId,
        CategoryId categoryId,
        ConfidenceLevel confidence,
        LocalDateTime occurredAt
) implements DomainEvent {

    public TransactionCategorized(TransactionId transactionId, CategoryId categoryId,
                                  ConfidenceLevel confidence) {
        this(transactionId, categoryId, confidence, LocalDateTime.now());
    }
}
