package com.wealthwise.presentation.dto;

import com.wealthwise.domain.category.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateCategoryRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String name,

        @NotNull(message = "Le type est obligatoire")
        CategoryType type,

        @NotBlank(message = "La couleur est obligatoire")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "La couleur doit être au format #RRGGBB")
        String color,

        String icon
) {
}
