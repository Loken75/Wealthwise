package com.wealthwise.domain.transaction.model;

import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.event.TransactionCategorized;
import com.wealthwise.domain.transaction.event.TransactionCreated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root du contexte Transaction.
 */
public class Transaction {

    private TransactionId id;
    private AccountId accountId;
    private Money amount;
    private String description;
    private LocalDate date;
    private TransactionType type;
    private CategoryId categoryId;
    private ConfidenceLevel confidenceLevel;
    private LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ========== Constructeurs ==========

    private Transaction() {
        // Utilisé par reconstitute()
    }

    // ========== Factory Methods ==========

    /**
     * Crée une nouvelle transaction. Valide et émet TransactionCreated.
     */
    public static Transaction create(AccountId accountId, Money amount, String description,
                                      LocalDate date, TransactionType type) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId must not be null");
        }
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Transaction description must not be blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Transaction date must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type must not be null");
        }

        Transaction tx = new Transaction();
        tx.id = TransactionId.generate();
        tx.accountId = accountId;
        tx.amount = amount;
        tx.description = description;
        tx.date = date;
        tx.type = type;
        tx.createdAt = LocalDateTime.now();

        tx.domainEvents.add(new TransactionCreated(
                tx.id, accountId, amount, type, date, LocalDateTime.now()
        ));

        return tx;
    }

    /**
     * Reconstitue une Transaction depuis la base de données.
     * Pas de validation ni d'événements.
     */
    public static Transaction reconstitute(TransactionId id, AccountId accountId, Money amount,
                                            String description, LocalDate date, TransactionType type,
                                            CategoryId categoryId, ConfidenceLevel confidenceLevel,
                                            LocalDateTime createdAt) {
        Transaction tx = new Transaction();
        tx.id = id;
        tx.accountId = accountId;
        tx.amount = amount;
        tx.description = description;
        tx.date = date;
        tx.type = type;
        tx.categoryId = categoryId;
        tx.confidenceLevel = confidenceLevel;
        tx.createdAt = createdAt;
        return tx;
    }

    // ========== Comportements métier ==========

    /**
     * Catégorise cette transaction (ou re-catégorise).
     */
    public void categorize(CategoryId categoryId, ConfidenceLevel confidence) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        if (confidence == null) {
            throw new IllegalArgumentException("ConfidenceLevel must not be null");
        }

        this.categoryId = categoryId;
        this.confidenceLevel = confidence;

        domainEvents.add(new TransactionCategorized(
                this.id, categoryId, confidence, LocalDateTime.now()
        ));
    }

    // ========== Méthodes de requête ==========

    public boolean isExpense() {
        return this.type == TransactionType.EXPENSE;
    }

    public boolean isIncome() {
        return this.type == TransactionType.INCOME;
    }

    public boolean isCategorized() {
        return this.categoryId != null;
    }

    // ========== Domain Events ==========

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // ========== Getters ==========

    public TransactionId getId() { return id; }
    public AccountId getAccountId() { return accountId; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public TransactionType getType() { return type; }
    public CategoryId getCategoryId() { return categoryId; }
    public ConfidenceLevel getConfidenceLevel() { return confidenceLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
