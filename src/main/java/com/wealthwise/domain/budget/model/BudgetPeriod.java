package com.wealthwise.domain.budget.model;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Période couverte par un budget (un mois).
 *
 * YearMonth est une classe Java 8+ qui représente un mois d'une année
 * (ex: 2026-02) sans jour ni heure. Parfait pour un budget mensuel.
 *
 * Le record calcule automatiquement le premier et dernier jour du mois
 * pour pouvoir vérifier si une date de transaction tombe dans la période.
 */
public record BudgetPeriod(YearMonth month) {

    public BudgetPeriod {
        if (month == null) {
            throw new IllegalArgumentException("Budget month must not be null");
        }
    }

    /**
     * Crée une période pour un mois donné.
     */
    public static BudgetPeriod of(YearMonth month) {
        return new BudgetPeriod(month);
    }

    /**
     * Crée une période pour le mois en cours.
     */
    public static BudgetPeriod currentMonth() {
        return new BudgetPeriod(YearMonth.now());
    }

    /**
     * Vérifie si une date tombe dans cette période.
     */
    public boolean contains(LocalDate date) {
        if (date == null) return false;
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Premier jour de la période.
     */
    public LocalDate startDate() {
        return month.atDay(1);
    }

    /**
     * Dernier jour de la période.
     */
    public LocalDate endDate() {
        return month.atEndOfMonth();
    }
}
