package com.wealthwise.domain.transaction.model;

import java.util.UUID;

/**
 * Identifiant unique d'une transaction.
 *
 * Même principe que AccountId : un identifiant typé pour éviter
 * de confondre un TransactionId avec un AccountId au niveau du compilateur.
 */
public record TransactionId(String value) {

    public TransactionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransactionId must not be null or blank");
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID().toString());
    }

    public static TransactionId of(String value) {
        return new TransactionId(value);
    }
}
