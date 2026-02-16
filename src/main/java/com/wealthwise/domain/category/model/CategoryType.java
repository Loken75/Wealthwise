package com.wealthwise.domain.category.model;

/**
 * Type de catégorie : indique si la catégorie s'applique
 * aux revenus ou aux dépenses.
 *
 * Exemples :
 * - EXPENSE : Alimentation, Transport, Loyer, Loisirs...
 * - INCOME  : Salaire, Freelance, Remboursement, Dividendes...
 */
public enum CategoryType {
    INCOME,
    EXPENSE
}
