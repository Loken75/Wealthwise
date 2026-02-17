package com.wealthwise.infrastructure.persistence.jpa;

import com.wealthwise.infrastructure.persistence.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, String> {

    boolean existsByNameIgnoreCase(String name);
}
