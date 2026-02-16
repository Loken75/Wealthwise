package com.wealthwise.domain.account.event;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.model.AccountType;

import java.time.LocalDateTime;

/**
 * Evenement emis quand un nouveau compte est cree.
 */
public record AccountCreated(
        AccountId accountId,
        String name,
        AccountType type,
        LocalDateTime occurredAt
) implements DomainEvent {

    public AccountCreated(AccountId accountId, String name, AccountType type) {
        this(accountId, name, type, LocalDateTime.now());
    }
}