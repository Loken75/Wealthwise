package com.wealthwise.domain.category.model;

import java.util.UUID;

/**
 * Identifiant unique d'une catégorie.
 *
 * Placé dans le contexte Category mais référencé par Transaction et Budget.
 * En DDD, les contextes ne se référencent que par ID, jamais par objet entier.
 * C'est ce qui permet de garder les contextes indépendants.
 */
public record CategoryId(String value) {

    public CategoryId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryId must not be null or blank");
        }
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID().toString());
    }

    public static CategoryId of(String value) {
        return new CategoryId(value);
    }
}
