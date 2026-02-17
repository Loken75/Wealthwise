package com.wealthwise.presentation.dto;

import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategorizeTransactionRequest(
        @NotBlank(message = "L'identifiant de la catégorie est obligatoire")
        String categoryId,

        @NotNull(message = "Le niveau de confiance est obligatoire")
        ConfidenceLevel confidenceLevel
) {
}
