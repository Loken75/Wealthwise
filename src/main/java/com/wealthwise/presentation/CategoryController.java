package com.wealthwise.presentation;

import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.presentation.dto.CategoryResponse;
import com.wealthwise.presentation.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur REST pour les catégories.
 *
 * Pas de Command Handler ici — la création de catégorie est simple
 * (pas d'orchestration multi-agrégats), donc le contrôleur appelle
 * directement le domaine et le repository. C'est acceptable pour
 * les opérations CRUD simples en DDD.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = Category.create(
                request.name(), request.type(), request.color(), request.icon()
        );

        categoryRepository.save(category);

        return ResponseEntity
                .created(URI.create("/api/categories/" + category.getId().value()))
                .body(CategoryResponse.from(category));
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable String id) {
        return categoryRepository.findById(CategoryId.of(id))
                .map(CategoryResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
