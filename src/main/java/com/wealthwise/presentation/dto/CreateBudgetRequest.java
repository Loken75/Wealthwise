package com.wealthwise.presentation.dto;

import com.wealthwise.domain.shared.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.YearMonth;

public record CreateBudgetRequest(
        @NotBlank(message = "L'identifiant de la catégorie est obligatoire")
        String categoryId,

        @NotNull(message = "La limite est obligatoire")
        @Positive(message = "La limite doit être positive")
        Double limitAmount,

        @NotNull(message = "La devise est obligatoire")
        Currency currency,

        @NotNull(message = "Le mois est obligatoire")
        YearMonth month
) {
}
