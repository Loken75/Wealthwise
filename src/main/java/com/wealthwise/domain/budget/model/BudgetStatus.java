package com.wealthwise.domain.budget.model;

/**
 * Statut d'un budget basé sur le pourcentage de consommation.
 *
 * ON_TRACK = Tout va bien, dépenses sous contrôle (< 80%)
 * WARNING  = Attention, on approche de la limite (80% - 100%)
 * EXCEEDED = Budget dépassé (> 100%)
 */
public enum BudgetStatus {
    ON_TRACK,
    WARNING,
    EXCEEDED
}
