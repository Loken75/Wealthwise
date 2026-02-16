package com.wealthwise.domain.category.model;

import java.time.LocalDateTime;

/**
 * Aggregate Root du contexte Category.
 *
 * Une catégorie est un label que l'utilisateur associe à ses transactions
 * pour les classer (Alimentation, Transport, Salaire, etc.).
 *
 * C'est l'agrégat le plus simple du domaine : pas de Domain Events
 * pour l'instant car la création/modification d'une catégorie n'a pas
 * d'impact sur les autres contextes. On en ajoutera si besoin (YAGNI).
 *
 * Règles métier :
 * - Le nom ne peut pas être vide
 * - La couleur doit être un code hexadécimal valide (#RRGGBB)
 * - L'icône est optionnelle mais le nom est obligatoire
 */
public class Category {

    private final CategoryId id;
    private String name;
    private final CategoryType type;
    private String color;
    private String icon;
    private final LocalDateTime createdAt;

    // ========== Constructeur privé ==========

    private Category(CategoryId id, String name, CategoryType type, String color, String icon) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.createdAt = LocalDateTime.now();
    }

    // ========== Factory Method ==========

    /**
     * Crée une nouvelle catégorie.
     *
     * @param name  le nom affiché (ex: "Alimentation")
     * @param type  INCOME ou EXPENSE
     * @param color code couleur hex (ex: "#FF5733")
     * @param icon  nom de l'icône (ex: "shopping-cart"), nullable
     */
    public static Category create(String name, CategoryType type, String color, String icon) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("CategoryType must not be null");
        }
        validateColor(color);

        return new Category(CategoryId.generate(), name, type, color, icon);
    }

    // ========== Comportements métier ==========

    /**
     * Renomme la catégorie.
     */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Category name must not be blank");
        }
        this.name = newName;
    }

    /**
     * Change la couleur de la catégorie.
     */
    public void changeColor(String newColor) {
        validateColor(newColor);
        this.color = newColor;
    }

    /**
     * Change l'icône de la catégorie.
     */
    public void changeIcon(String newIcon) {
        this.icon = newIcon;
    }

    // ========== Validation ==========

    private static void validateColor(String color) {
        if (color == null || !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException(
                    "Color must be a valid hex code (#RRGGBB), got: " + color
            );
        }
    }

    // ========== Getters ==========

    public CategoryId getId() { return id; }
    public String getName() { return name; }
    public CategoryType getType() { return type; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
