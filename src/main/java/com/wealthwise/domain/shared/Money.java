package com.wealthwise.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object représentant une somme d'argent avec sa devise.
 *
 * Immutable grâce au record Java. Toute opération retourne un NOUVEAU Money.
 * Utilise BigDecimal pour éviter les erreurs d'arrondi des doubles.
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(currency, "Currency must not be null");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    // ========== Factory Methods ==========

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    /**
     * Crée un Money à zéro pour la devise donnée.
     * Utile pour initialiser un solde ou un accumulateur.
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    // ========== Opérations arithmétiques ==========
    // Chaque opération retourne un NOUVEAU Money (immutabilité).

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Inverse le signe : 100 → -100, -50 → 50.
     * Utile pour transformer un revenu en dépense et inversement.
     */
    public Money negate() {
        return new Money(this.amount.negate(), this.currency);
    }

    // ========== Comparaisons ==========

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    // ========== Méthode interne ==========

    /**
     * Vérifie que les deux Money ont la même devise.
     * On ne peut pas additionner des euros et des dollars sans taux de change.
     * "require" est une convention de nommage pour les méthodes de validation
     * qui lèvent une exception si la condition n'est pas remplie.
     */
    private void requireSameCurrency(Money other) {
        if (this.currency != other.currency) {
            throw new IllegalArgumentException(
                    "Cannot operate on different currency: %s vs %s"
                            .formatted(this.currency, other.currency)
            );
        }
    }
}
