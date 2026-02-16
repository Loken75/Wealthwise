package com.wealthwise.domain.account.model;

import com.wealthwise.domain.account.event.AccountBalanceUpdated;
import com.wealthwise.domain.account.event.AccountCreated;
import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root du contexte Account.
 *
 * DIFFERENCE CLE avec un Value Object :
 * - Un Value Object (Money, Balance) est un record immutable sans identite.
 * - Un Aggregate Root est une CLASSE MUTABLE avec une identite (AccountId).
 *   Deux comptes avec le meme nom ne sont pas "le meme compte".
 *
 * L'Aggregate Root est le GARDIEN des invariants metier :
 * - On ne peut pas debiter plus que le solde
 * - On ne peut pas operer sur un compte ferme
 * - On ne peut pas melanger les devises
 *
 * Il collecte les Domain Events dans une liste interne.
 * La couche Application les recupere et les publie apres la persistance.
 *
 * NOTE : ce n'est PAS un record car un Aggregate est mutable
 * (son etat change via credit/debit/close). C'est une classe classique.
 */
public class Account {

    private final AccountId id;
    private final String name;
    private final AccountType type;
    private final Currency currency;
    private final LocalDateTime createdAt;
    private Balance balance;
    private boolean closed;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ========== Constructeur prive ==========

    private Account(AccountId id, String name, AccountType type, Currency currency) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = Balance.zero(currency);
        this.closed = false;
        this.createdAt = LocalDateTime.now();
    }

    // ========== Factory Method ==========

    public static Account create(String name, AccountType type, Currency currency) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be null or blank");
        }

        Account account = new Account(AccountId.generate(), name, type, currency);
        account.domainEvents.add(new AccountCreated(account.id, name, type));
        return account;
    }

    // ========== Comportements metier ==========

    public void credit(Money amount) {
        requireOpen();
        requireSameCurrency(amount);

        Money previousBalance = this.balance.money();
        this.balance = this.balance.credit(amount);

        domainEvents.add(new AccountBalanceUpdated(
                this.id, previousBalance, this.balance.money()
        ));
    }

    public void debit(Money amount) {
        requireOpen();
        requireSameCurrency(amount);

        if (!this.balance.canDebit(amount)) {
            throw new IllegalStateException(
                    "Insufficient funds: balance is %s, tried to debit %s"
                            .formatted(this.balance.money().amount(), amount.amount())
            );
        }

        Money previousBalance = this.balance.money();
        this.balance = this.balance.debit(amount);

        domainEvents.add(new AccountBalanceUpdated(
                this.id, previousBalance, this.balance.money()
        ));
    }

    public void close() {
        if (this.closed) {
            throw new IllegalStateException("Account is already closed");
        }
        if (!this.balance.money().isZero()) {
            throw new IllegalStateException(
                    "Cannot close account with non-zero balance: " + this.balance.money().amount()
            );
        }
        this.closed = true;
    }

    // ========== Domain Events ==========

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // ========== Getters ==========

    public AccountId getId() { return id; }
    public String getName() { return name; }
    public AccountType getType() { return type; }
    public Currency getCurrency() { return currency; }
    public Balance getBalance() { return balance; }
    public boolean isClosed() { return closed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ========== Methodes internes ==========

    private void requireOpen() {
        if (this.closed) {
            throw new IllegalStateException("Cannot operate on a closed account");
        }
    }

    private void requireSameCurrency(Money amount) {
        if (amount.currency() != this.currency) {
            throw new IllegalArgumentException(
                    "Expected currency %s but got %s"
                            .formatted(this.currency, amount.currency())
            );
        }
    }
}