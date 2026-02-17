package com.wealthwise.application.command;

import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;

/**
 * Commande pour créer un nouveau compte.
 *
 * En CQRS, une Command représente une INTENTION de modifier l'état du système.
 * C'est un simple record immutable qui transporte les données nécessaires.
 *
 * Convention de nommage : verbe à l'impératif + nom → CreateAccount
 * (contrairement aux Events qui sont au passé : AccountCreated)
 *
 * Une Command ne contient JAMAIS de logique métier.
 * C'est un simple conteneur de données (DTO = Data Transfer Object).
 */
public record CreateAccountCommand(
        String name,
        AccountType type,
        Currency currency
) {
}
