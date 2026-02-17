package com.wealthwise.infrastructure.persistence.jpa;

import com.wealthwise.infrastructure.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository Spring Data JPA pour les transactions.
 *
 * Spring Data JPA peut DEVINER le SQL à partir du nom de la méthode !
 * "findByAccountId" → SELECT * FROM transactions WHERE account_id = ?
 * "findByDateBetween" → SELECT * FROM transactions WHERE date BETWEEN ? AND ?
 *
 * C'est la convention "Query Derivation" de Spring Data.
 * Le nom de la méthode EST la requête.
 */
public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByAccountId(String accountId);

    List<TransactionEntity> findByDateBetween(LocalDate start, LocalDate end);

    List<TransactionEntity> findByAccountIdAndDateBetween(String accountId, LocalDate start, LocalDate end);

    List<TransactionEntity> findByCategoryId(String categoryId);
}
