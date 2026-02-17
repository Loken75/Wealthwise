package com.wealthwise.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class BudgetEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal limitAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "spent", nullable = false, precision = 19, scale = 4)
    private BigDecimal spent;

    @Column(name = "period_month", nullable = false)
    private String periodMonth;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected BudgetEntity() {
    }

    public BudgetEntity(String id, String categoryId, BigDecimal limitAmount, String currency,
                        BigDecimal spent, String periodMonth, String status, LocalDateTime createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.currency = currency;
        this.spent = spent;
        this.periodMonth = periodMonth;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getCategoryId() { return categoryId; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public String getCurrency() { return currency; }
    public BigDecimal getSpent() { return spent; }
    public String getPeriodMonth() { return periodMonth; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
