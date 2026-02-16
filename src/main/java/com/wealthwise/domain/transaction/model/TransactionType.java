package com.wealthwise.domain.transaction.model;

/**
 * Type de transaction financière.
 *
 * INCOME   = Revenu (salaire, remboursement, vente...)
 * EXPENSE  = Dépense (courses, loyer, abonnement...)
 * TRANSFER = Virement entre comptes (pas encore implémenté)
 */
public enum TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}
