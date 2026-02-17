package com.wealthwise.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité JPA pour la table "accounts" en base de données.
 *
 * @Entity indique à JPA que cette classe correspond à une table SQL.
 * @Table(name = "accounts") spécifie le nom de la table.
 *
 * IMPORTANT : cette classe est DISTINCTE de l'objet domaine Account.
 * Elle ne contient aucune logique métier, seulement la structure de la table.
 *
 * Pourquoi séparer l'entité JPA du domaine ?
 * - Le domaine ne doit pas dépendre de JPA (architecture hexagonale)
 * - La structure de la table peut différer de l'objet domaine
 *   (ex: Balance est "aplati" en un simple BigDecimal dans la table)
 * - On peut changer de technologie de persistance sans toucher au domaine
 *
 * JPA exige :
 * - Un constructeur sans argument (peut être protected)
 * - Des getters/setters (ou accès par champs avec @Access)
 * - Un champ @Id
 */
@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Sans cela, JPA utiliserait l'ordinal (0, 1, 2...) ce qui est fragile
     * (si on réordonne l'enum, les données existantes deviennent fausses).
     */
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "closed", nullable = false)
    private boolean closed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructeur sans argument requis par JPA
    protected AccountEntity() {
    }

    // Constructeur complet pour le mapping depuis le domaine
    public AccountEntity(String id, String name, String type, String currency,
                         BigDecimal balance, boolean closed, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.closed = closed;
        this.createdAt = createdAt;
    }

    // Getters (JPA en a besoin pour lire les champs)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public boolean isClosed() { return closed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
