package com.wealthwise.domain.account.model;

/**
 * Types de comptes financiers supportés.
 *
 * CHECKING  = Compte courant (le classique pour les dépenses quotidiennes)
 * SAVINGS   = Livret d'épargne (Livret A, LDD, etc.)
 * CREDIT_CARD = Carte de crédit (solde souvent négatif)
 * CASH      = Espèces (portefeuille physique)
 */
public enum AccountType {
    CHECKING,
    SAVINGS,
    CREDIT_CARD,
    CASH
}
