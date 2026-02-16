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
 * Un Budget définit une limite de dépenses pour une catégorie donnée
 * sur une période (un mois). Par exemple : "200€ max en Alimentation en février 2026".
 *
 * Le Budget suit les dépenses enregistrées et change de statut automatiquement :
 * - ON_TRACK : < 80% de la limite
 * - WARNING  : entre 80% et 100%
 * - EXCEEDED : > 100%
 *
 * Les seuils (80% pour WARNING) sont des constantes métier modifiables.
 * On pourrait les rendre configurables par utilisateur plus tard.
 *
 * Règles métier :
 * - La limite doit être positive
 * - Le montant dépensé ne peut pas être négatif
 * - Les événements WARNING et EXCEEDED ne sont émis qu'une seule fois
 *   (pas à chaque dépense qui maintient le même statut)
 */
public class Budget {

    private static final double WARNING_THRESHOLD = 0.80; // 80%

    private final BudgetId id;
    private final CategoryId categoryId;
    private final Money limit;
    private final BudgetPeriod period;
    private final Currency currency;
    private final LocalDateTime createdAt;

    private Money spent;
    private BudgetStatus status;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ========== Constructeur privé ==========

    private Budget(BudgetId id, CategoryId categoryId, Money limit, BudgetPeriod period) {
        this.id = id;
        this.categoryId = categoryId;
        this.limit = limit;
        this.period = period;
        this.currency = limit.currency();
        this.spent = Money.zero(limit.currency());
        this.status = BudgetStatus.ON_TRACK;
        this.createdAt = LocalDateTime.now();
    }

    // ========== Factory Method ==========

    /**
     * Crée un nouveau budget mensuel pour une catégorie.
     *
     * @param categoryId la catégorie concernée
     * @param limit      le montant maximum autorisé (doit être positif)
     * @param period     la période du budget (un mois)
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

        return new Budget(BudgetId.generate(), categoryId, limit, period);
    }

    // ========== Comportements métier ==========

    /**
     * Enregistre une dépense dans ce budget.
     * Met à jour le montant dépensé et recalcule le statut.
     * Émet des événements si un seuil est franchi.
     *
     * @param amount le montant de la dépense (doit être positif)
     */
    public void recordExpense(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        if (amount.currency() != this.currency) {
            throw new IllegalArgumentException(
                    "Expected currency %s but got %s"
                            .formatted(this.currency, amount.currency())
            );
        }

        this.spent = this.spent.add(amount);
        updateStatus();
    }

    /**
     * Retourne le montant restant avant d'atteindre la limite.
     * Peut être négatif si le budget est dépassé.
     */
    public Money getRemainingAmount() {
        return this.limit.subtract(this.spent);
    }

    /**
     * Retourne le pourcentage de consommation du budget (0.0 à 1.0+).
     * Exemple : 0.75 = 75% du budget utilisé.
     */
    public double getUsagePercentage() {
        if (this.limit.isZero()) return 0.0;
        return this.spent.amount()
                .divide(this.limit.amount(), 4, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    // ========== Logique interne ==========

    /**
     * Recalcule le statut du budget et émet des événements si nécessaire.
     * Les événements ne sont émis que lors d'un CHANGEMENT de statut,
     * pas à chaque dépense.
     */
    private void updateStatus() {
        BudgetStatus previousStatus = this.status;
        double usage = getUsagePercentage();

        if (usage > 1.0) {
            this.status = BudgetStatus.EXCEEDED;
        } else if (usage >= WARNING_THRESHOLD) {
            this.status = BudgetStatus.WARNING;
        } else {
            this.status = BudgetStatus.ON_TRACK;
        }

        // Émettre un événement seulement si le statut a changé
        if (this.status != previousStatus) {
            if (this.status == BudgetStatus.EXCEEDED) {
                domainEvents.add(new BudgetExceeded(this.id, this.limit, this.spent));
            } else if (this.status == BudgetStatus.WARNING) {
                domainEvents.add(new BudgetWarningReached(this.id, usage));
            }
        }
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
    public Money getSpent() { return spent; }
    public BudgetPeriod getPeriod() { return period; }
    public BudgetStatus getStatus() { return status; }
    public Currency getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
