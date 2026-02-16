package com.wealthwise.domain.account.model;

import java.util.UUID;

/**
 * Identifiant unique d'un compte.
 *
 * En DDD, chaque Aggregate Root a un identifiant typé (pas un simple String).
 * Ça évite les erreurs du genre : passer un TransactionId là où on attend un AccountId.
 * Le compilateur attrape l'erreur au lieu de la découvrir en production.
 *
 * C'est un record avec un seul champ "value" — léger et immutable.
 */
public record AccountId(String value) {

    public AccountId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AccountId must not be null or blank");
        }
    }

    /**
     * Génère un nouvel identifiant unique basé sur UUID.
     * UUID (Universally Unique Identifier) : chaîne de 36 caractères
     * quasi-impossible à dupliquer, même entre machines différentes.
     */
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }

    /**
     * Recrée un AccountId à partir d'une valeur existante (ex: depuis la base de données).
     */
    public static AccountId of(String value) {
        return new AccountId(value);
    }
}
