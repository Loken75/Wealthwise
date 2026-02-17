package com.wealthwise.unit.infrastructure.fake;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.port.CategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation en mémoire du CategoryRepository pour les tests.
 */
public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<String, Category> store = new HashMap<>();

    @Override
    public Category save(Category category) {
        store.put(category.getId().value(), category);
        return category;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsByName(String name) {
        return store.values().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(name));
    }

    @Override
    public void deleteById(CategoryId id) {
        store.remove(id.value());
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
