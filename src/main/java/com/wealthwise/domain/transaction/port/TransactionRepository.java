package com.wealthwise.domain.transaction.port;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des transactions.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findByAccountId(AccountId accountId);

    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);

    List<Transaction> findByAccountIdAndDateBetween(AccountId accountId, LocalDate start, LocalDate end);

    List<Transaction> findByCategoryId(CategoryId categoryId);

    void deleteById(TransactionId id);
}
