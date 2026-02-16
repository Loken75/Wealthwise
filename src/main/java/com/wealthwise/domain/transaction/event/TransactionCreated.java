package com.wealthwise.domain.transaction.event;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.model.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Événement émis quand une nouvelle transaction est créée.
 *
 * Cet événement est écouté par :
 * - Le contexte Account pour mettre à jour le solde
 * - Le contexte Budget pour vérifier les seuils
 * - Le contexte Analytics pour mettre à jour les rapports
 */
public record TransactionCreated(
        TransactionId transactionId,
        AccountId accountId,
        Money amount,
        TransactionType type,
        LocalDate date,
        LocalDateTime occurredAt
) implements DomainEvent {

    public TransactionCreated(TransactionId transactionId, AccountId accountId,
                              Money amount, TransactionType type, LocalDate date) {
        this(transactionId, accountId, amount, type, date, LocalDateTime.now());
    }
}
