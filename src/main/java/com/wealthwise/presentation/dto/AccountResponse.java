package com.wealthwise.presentation.dto;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un compte.
 *
 * On ne retourne JAMAIS directement un objet du domaine dans l'API.
 * Le DTO permet de :
 * - Contrôler exactement quels champs sont exposés
 * - Découpler le format de l'API de la structure du domaine
 * - Aplatir les objets imbriqués (Balance → balance en BigDecimal)
 *
 * La méthode static from() convertit un objet domaine en DTO.
 * C'est un pattern courant appelé "factory method de mapping".
 */
public record AccountResponse(
        String id,
        String name,
        AccountType type,
        Currency currency,
        BigDecimal balance,
        boolean closed,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId().value(),
                account.getName(),
                account.getType(),
                account.getCurrency(),
                account.getBalance().money().amount(),
                account.isClosed(),
                account.getCreatedAt()
        );
    }
}
