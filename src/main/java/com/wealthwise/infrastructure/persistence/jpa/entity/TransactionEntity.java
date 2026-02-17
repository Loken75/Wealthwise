package com.wealthwise.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "category_id", length = 36)
    private String categoryId;

    @Column(name = "confidence_level")
    private String confidenceLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected TransactionEntity() {
    }

    public TransactionEntity(String id, String accountId, BigDecimal amount, String currency,
                             String description, LocalDate date, String type,
                             String categoryId, String confidenceLevel, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.date = date;
        this.type = type;
        this.categoryId = categoryId;
        this.confidenceLevel = confidenceLevel;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public String getType() { return type; }
    public String getCategoryId() { return categoryId; }
    public String getConfidenceLevel() { return confidenceLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
