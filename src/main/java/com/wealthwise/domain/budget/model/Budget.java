package com.wealthwise.domain.budget.model;

import com.wealthwise.domain.account.event.DomainEvent;
import com.wealthwise.domain.budget.event.BudgetExceeded;
import com.wealthwise.domain.budget.event.BudgetWarningReached;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root du contexte Budget.
 *
 * Gère un budget mensuel pour une catégorie de dépenses.
 * Émet des événements quand des seuils sont atteints (80% et 100%).
 */
public class Budget {

    /** Seuil d'avertissement : 80% du budget utilisé */
    public static final double WARNING_THRESHOLD = 0.80;

    private BudgetId id;
    private CategoryId categoryId;
    private Money limit;
    private BudgetPeriod period;
    private Money spent;
    private BudgetStatus status;
    private LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ========== Constructeurs ==========

    private Budget() {
        // Utilisé par reconstitute()
    }

    // ========== Factory Methods ==========

    /**
     * Crée un nouveau budget. Valide les paramètres.
     */
    public static Budget create(CategoryId categoryId, Money limit, BudgetPeriod period) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        if (limit == null || !limit.isPositive()) {
            throw new IllegalArgumentException("Budget limit must be positive");
        }
        if (period == null) {
            throw new IllegalArgumentException("BudgetPeriod must not be null");
        }

        Budget budget = new Budget();
        budget.id = BudgetId.generate();
        budget.categoryId = categoryId;
        budget.limit = limit;
        budget.period = period;
        budget.spent = Money.zero(limit.currency());
        budget.status = BudgetStatus.ON_TRACK;
        budget.createdAt = LocalDateTime.now();
        return budget;
    }

    /**
     * Reconstitue un Budget depuis la base de données.
     * Pas de validation ni d'événements.
     */
    public static Budget reconstitute(BudgetId id, CategoryId categoryId, Money limit,
                                       BudgetPeriod period, Money spent, BudgetStatus status,
                                       LocalDateTime createdAt) {
        Budget budget = new Budget();
        budget.id = id;
        budget.categoryId = categoryId;
        budget.limit = limit;
        budget.period = period;
        budget.spent = spent;
        budget.status = status;
        budget.createdAt = createdAt;
        return budget;
    }

    // ========== Comportements métier ==========

    /**
     * Enregistre une dépense dans ce budget.
     * Met à jour le statut et émet des événements si des seuils sont franchis.
     */
    public void recordExpense(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }

        this.spent = this.spent.add(amount);
        updateStatus();
    }

    /**
     * Calcule le montant restant (peut être négatif si dépassé).
     */
    public Money getRemainingAmount() {
        return this.limit.subtract(this.spent);
    }

    /**
     * Calcule le pourcentage d'utilisation (0.0 à 1.0+).
     */
    public double getUsagePercentage() {
        if (this.limit.isZero()) {
            return 0.0;
        }
        return this.spent.amount().doubleValue() / this.limit.amount().doubleValue();
    }

    // ========== Domain Events ==========

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // ========== Getters ==========

    public BudgetId getId() { return id; }
    public CategoryId getCategoryId() { return categoryId; }
    public Money getLimit() { return limit; }
    public BudgetPeriod getPeriod() { return period; }
    public Money getSpent() { return spent; }
    public BudgetStatus getStatus() { return status; }
    public Currency getCurrency() { return limit.currency(); }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ========== Méthodes internes ==========

    /**
     * Met à jour le statut et émet des événements UNIQUEMENT au changement de seuil.
     */
    private void updateStatus() {
        double usage = getUsagePercentage();
        BudgetStatus previousStatus = this.status;

        if (usage >= 1.0) {
            this.status = BudgetStatus.EXCEEDED;
            if (previousStatus != BudgetStatus.EXCEEDED) {
                domainEvents.add(new BudgetExceeded(
                        this.id, this.limit, this.spent, LocalDateTime.now()
                ));
            }
        } else if (usage >= WARNING_THRESHOLD) {
            this.status = BudgetStatus.WARNING;
            if (previousStatus == BudgetStatus.ON_TRACK) {
                domainEvents.add(new BudgetWarningReached(
                        this.id, usage, LocalDateTime.now()
                ));
            }
        }
    }
}
