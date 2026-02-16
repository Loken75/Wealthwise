package com.wealthwise.domain.transaction.model;

/**
 * Niveau de confiance de la catégorisation automatique.
 *
 * Quand le système catégorise automatiquement une transaction
 * (ex: "Carrefour" → Alimentation), il indique sa confiance :
 *
 * HIGH   = Correspondance exacte avec une règle ou historique clair (>80%)
 * MEDIUM = Correspondance partielle, l'utilisateur devrait confirmer (50-80%)
 * LOW    = Suggestion incertaine, probablement faux (<50%)
 * MANUAL = L'utilisateur a choisi la catégorie lui-même (100% fiable)
 */
public enum ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW,
    MANUAL
}
