package com.wealthwise.application.command;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.transaction.model.TransactionType;

import java.time.LocalDate;

/**
 * Commande pour créer une nouvelle transaction.
 */
public record CreateTransactionCommand(
        AccountId accountId,
        double amount,
        Currency currency,
        String description,
        LocalDate date,
        TransactionType type
) {
}
