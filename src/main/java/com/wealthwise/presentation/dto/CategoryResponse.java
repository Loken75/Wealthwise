package com.wealthwise.presentation.dto;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        String id,
        String name,
        CategoryType type,
        String color,
        String icon,
        LocalDateTime createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId().value(),
                category.getName(),
                category.getType(),
                category.getColor(),
                category.getIcon(),
                category.getCreatedAt()
        );
    }
}
