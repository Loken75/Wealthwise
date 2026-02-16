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
 *
 * Représente une opération financière (revenu, dépense, virement).
 * Chaque Transaction est liée à un Account (par AccountId, pas par référence directe).
 *
 * Règles métier :
 * - Le montant doit être positif (le type INCOME/EXPENSE indique le sens)
 * - La description ne peut pas être vide
 * - La catégorisation peut être automatique (avec niveau de confiance) ou manuelle
 * - Une transaction catégorisée peut être re-catégorisée (changement d'avis)
 */
public class Transaction {

    private final TransactionId id;
    private final AccountId accountId;
    private final Money amount;
    private final String description;
    private final LocalDate date;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    // Catégorisation (nullable au départ : une transaction peut ne pas être catégorisée)
    private CategoryId categoryId;
    private ConfidenceLevel confidenceLevel;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ========== Constructeur privé ==========

    private Transaction(TransactionId id, AccountId accountId, Money amount,
                        String description, LocalDate date, TransactionType type) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // ========== Factory Method ==========

    /**
     * Crée une nouvelle transaction.
     *
     * @param accountId   le compte associé (référence par ID)
     * @param amount      le montant (doit être positif)
     * @param description la description (ex: "Courses Carrefour")
     * @param date        la date de la transaction
     * @param type        INCOME, EXPENSE ou TRANSFER
     */
    public static Transaction create(AccountId accountId, Money amount, String description,
                                     LocalDate date, TransactionType type) {
        // Validations
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

        Transaction tx = new Transaction(
                TransactionId.generate(), accountId, amount, description, date, type
        );
        tx.domainEvents.add(new TransactionCreated(
                tx.id, accountId, amount, type, date
        ));
        return tx;
    }

    // ========== Comportements métier ==========

    /**
     * Catégorise (ou re-catégorise) cette transaction.
     *
     * @param categoryId      la catégorie à associer
     * @param confidenceLevel le niveau de confiance (MANUAL si fait par l'utilisateur)
     */
    public void categorize(CategoryId categoryId, ConfidenceLevel confidenceLevel) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        if (confidenceLevel == null) {
            throw new IllegalArgumentException("ConfidenceLevel must not be null");
        }

        this.categoryId = categoryId;
        this.confidenceLevel = confidenceLevel;

        domainEvents.add(new TransactionCategorized(this.id, categoryId, confidenceLevel));
    }

    /**
     * Vérifie si cette transaction est une dépense.
     */
    public boolean isExpense() {
        return this.type == TransactionType.EXPENSE;
    }

    /**
     * Vérifie si cette transaction est un revenu.
     */
    public boolean isIncome() {
        return this.type == TransactionType.INCOME;
    }

    /**
     * Vérifie si cette transaction a été catégorisée.
     */
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
