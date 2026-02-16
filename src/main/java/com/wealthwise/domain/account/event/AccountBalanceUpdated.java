package com.wealthwise.domain.account.event;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.shared.Money;

import java.time.LocalDateTime;

/**
 * Evenement emis quand le solde d'un compte change.
 */
public record AccountBalanceUpdated(
        AccountId accountId,
        Money previousBalance,
        Money newBalance,
        LocalDateTime occurredAt
) implements DomainEvent {

    public AccountBalanceUpdated(AccountId accountId, Money previousBalance, Money newBalance) {
        this(accountId, previousBalance, newBalance, LocalDateTime.now());
    }
}