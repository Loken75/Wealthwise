package com.wealthwise.infrastructure.persistence.jpa.mapper;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.model.CategoryType;
import com.wealthwise.infrastructure.persistence.jpa.entity.CategoryEntity;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.getId().value(),
                category.getName(),
                category.getType().name(),
                category.getColor(),
                category.getIcon(),
                category.getCreatedAt()
        );
    }

    public static Category toDomain(CategoryEntity entity) {
        return Category.reconstitute(
                CategoryId.of(entity.getId()),
                entity.getName(),
                CategoryType.valueOf(entity.getType()),
                entity.getColor(),
                entity.getIcon(),
                entity.getCreatedAt()
        );
    }
}
