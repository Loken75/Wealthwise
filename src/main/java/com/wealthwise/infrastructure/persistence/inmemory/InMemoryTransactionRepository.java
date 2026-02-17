package com.wealthwise.infrastructure.persistence.inmemory;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.port.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        store.put(transaction.getId().value(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<Transaction> findByAccountId(AccountId accountId) {
        return store.values().stream()
                .filter(tx -> tx.getAccountId().equals(accountId))
                .toList();
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDate start, LocalDate end) {
        return store.values().stream()
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();
    }

    @Override
    public List<Transaction> findByAccountIdAndDateBetween(AccountId accountId, LocalDate start, LocalDate end) {
        return store.values().stream()
                .filter(tx -> tx.getAccountId().equals(accountId))
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();
    }

    @Override
    public List<Transaction> findByCategoryId(CategoryId categoryId) {
        return store.values().stream()
                .filter(tx -> categoryId.equals(tx.getCategoryId()))
                .toList();
    }

    @Override
    public void deleteById(TransactionId id) {
        store.remove(id.value());
    }
}
