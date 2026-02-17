package com.wealthwise.infrastructure.persistence.jpa.adapter;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.port.TransactionRepository;
import com.wealthwise.infrastructure.persistence.jpa.SpringDataTransactionRepository;
import com.wealthwise.infrastructure.persistence.jpa.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionRepositoryAdapter implements TransactionRepository {

    private final SpringDataTransactionRepository jpaRepository;

    public JpaTransactionRepositoryAdapter(SpringDataTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        jpaRepository.save(TransactionMapper.toEntity(transaction));
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value())
                .map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(AccountId accountId) {
        return jpaRepository.findByAccountId(accountId.value()).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDate start, LocalDate end) {
        return jpaRepository.findByDateBetween(start, end).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByAccountIdAndDateBetween(AccountId accountId, LocalDate start, LocalDate end) {
        return jpaRepository.findByAccountIdAndDateBetween(accountId.value(), start, end).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByCategoryId(CategoryId categoryId) {
        return jpaRepository.findByCategoryId(categoryId.value()).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(TransactionId id) {
        jpaRepository.deleteById(id.value());
    }
}
