package com.wealthwise.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "color", nullable = false, length = 7)
    private String color;

    @Column(name = "icon")
    private String icon;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected CategoryEntity() {
    }

    public CategoryEntity(String id, String name, String type, String color,
                          String icon, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
