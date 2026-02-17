package com.wealthwise.presentation.dto;

import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de requête pour la création d'un compte.
 *
 * Un DTO (Data Transfer Object) est un objet qui transporte des données
 * entre la couche Présentation (API) et la couche Application.
 *
 * Les annotations @NotBlank et @NotNull viennent de Jakarta Validation
 * (anciennement javax.validation). Spring les vérifie automatiquement
 * quand on met @Valid sur le paramètre du controller.
 *
 * Le DTO est un record car c'est un simple conteneur de données immutable.
 * Jackson (la bibliothèque JSON de Spring) sait désérialiser du JSON
 * directement en record Java.
 */
public record CreateAccountRequest(
        @NotBlank(message = "Le nom du compte est obligatoire")
        String name,

        @NotNull(message = "Le type de compte est obligatoire")
        AccountType type,

        @NotNull(message = "La devise est obligatoire")
        Currency currency
) {
}
