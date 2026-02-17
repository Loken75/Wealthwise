package com.wealthwise.domain.category.port;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des catégories.
 */
public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(CategoryId id);

    List<Category> findAll();

    boolean existsByName(String name);

    void deleteById(CategoryId id);
}
