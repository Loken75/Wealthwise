package com.wealthwise.infrastructure.persistence.jpa.adapter;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.infrastructure.persistence.jpa.SpringDataCategoryRepository;
import com.wealthwise.infrastructure.persistence.jpa.mapper.CategoryMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository jpaRepository;

    public JpaCategoryRepositoryAdapter(SpringDataCategoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Category save(Category category) {
        jpaRepository.save(CategoryMapper.toEntity(category));
        return category;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
                .map(CategoryMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public void deleteById(CategoryId id) {
        jpaRepository.deleteById(id.value());
    }
}
