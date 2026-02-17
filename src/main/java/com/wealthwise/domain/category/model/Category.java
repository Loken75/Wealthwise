package com.wealthwise.domain.category.model;

import java.time.LocalDateTime;

/**
 * Aggregate Root du contexte Category.
 */
public class Category {

    private CategoryId id;
    private String name;
    private CategoryType type;
    private String color;
    private String icon;
    private LocalDateTime createdAt;

    // ========== Constructeurs ==========

    private Category() {
        // Utilisé par reconstitute()
    }

    // ========== Factory Methods ==========

    /**
     * Crée une nouvelle catégorie. Valide les paramètres.
     */
    public static Category create(String name, CategoryType type, String color, String icon) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("CategoryType must not be null");
        }
        if (color == null || !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Color must be a valid hex color (#RRGGBB format), got: " + color);
        }

        Category category = new Category();
        category.id = CategoryId.generate();
        category.name = name;
        category.type = type;
        category.color = color;
        category.icon = icon;
        category.createdAt = LocalDateTime.now();
        return category;
    }

    /**
     * Reconstitue une Category depuis la base de données.
     * Pas de validation — les données sont déjà valides.
     */
    public static Category reconstitute(CategoryId id, String name, CategoryType type,
                                         String color, String icon, LocalDateTime createdAt) {
        Category category = new Category();
        category.id = id;
        category.name = name;
        category.type = type;
        category.color = color;
        category.icon = icon;
        category.createdAt = createdAt;
        return category;
    }

    // ========== Comportements métier ==========

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
        this.name = newName;
    }

    public void changeColor(String newColor) {
        if (newColor == null || !newColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Color must be a valid hex color (#RRGGBB format), got: " + newColor);
        }
        this.color = newColor;
    }

    public void changeIcon(String newIcon) {
        this.icon = newIcon;
    }

    // ========== Getters ==========

    public CategoryId getId() { return id; }
    public String getName() { return name; }
    public CategoryType getType() { return type; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
