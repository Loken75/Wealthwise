package com.wealthwise.infrastructure.persistence.jpa.mapper;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.model.TransactionType;
import com.wealthwise.infrastructure.persistence.jpa.entity.TransactionEntity;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionEntity toEntity(Transaction tx) {
        return new TransactionEntity(
                tx.getId().value(),
                tx.getAccountId().value(),
                tx.getAmount().amount(),
                tx.getAmount().currency().name(),
                tx.getDescription(),
                tx.getDate(),
                tx.getType().name(),
                tx.getCategoryId() != null ? tx.getCategoryId().value() : null,
                tx.getConfidenceLevel() != null ? tx.getConfidenceLevel().name() : null,
                tx.getCreatedAt()
        );
    }

    public static Transaction toDomain(TransactionEntity entity) {
        CategoryId categoryId = entity.getCategoryId() != null
                ? CategoryId.of(entity.getCategoryId()) : null;
        ConfidenceLevel confidence = entity.getConfidenceLevel() != null
                ? ConfidenceLevel.valueOf(entity.getConfidenceLevel()) : null;

        return Transaction.reconstitute(
                TransactionId.of(entity.getId()),
                AccountId.of(entity.getAccountId()),
                Money.of(entity.getAmount().doubleValue(), Currency.valueOf(entity.getCurrency())),
                entity.getDescription(),
                entity.getDate(),
                TransactionType.valueOf(entity.getType()),
                categoryId,
                confidence,
                entity.getCreatedAt()
        );
    }
}
