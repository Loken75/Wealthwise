package com.wealthwise.infrastructure.persistence.inmemory;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.port.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<String, Category> store = new ConcurrentHashMap<>();

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
}
