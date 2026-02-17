package com.wealthwise.presentation.dto;

import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String accountId,
        BigDecimal amount,
        Currency currency,
        String description,
        LocalDate date,
        TransactionType type,
        String categoryId,
        ConfidenceLevel confidenceLevel,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId().value(),
                tx.getAccountId().value(),
                tx.getAmount().amount(),
                tx.getAmount().currency(),
                tx.getDescription(),
                tx.getDate(),
                tx.getType(),
                tx.getCategoryId() != null ? tx.getCategoryId().value() : null,
                tx.getConfidenceLevel(),
                tx.getCreatedAt()
        );
    }
}
