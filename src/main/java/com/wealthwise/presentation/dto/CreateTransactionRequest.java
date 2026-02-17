package com.wealthwise.presentation.dto;

import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.transaction.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotBlank(message = "L'identifiant du compte est obligatoire")
        String accountId,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être positif")
        Double amount,

        @NotNull(message = "La devise est obligatoire")
        Currency currency,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        @NotNull(message = "Le type est obligatoire")
        TransactionType type
) {
}
