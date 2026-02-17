package com.wealthwise.infrastructure.persistence.jpa.adapter;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;
import com.wealthwise.infrastructure.persistence.jpa.SpringDataAccountRepository;
import com.wealthwise.infrastructure.persistence.jpa.mapper.AccountMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptateur JPA pour le port AccountRepository.
 *
 * Cette classe est le PONT entre le domaine et JPA :
 * - Elle implémente l'interface du domaine (AccountRepository)
 * - Elle utilise Spring Data JPA (SpringDataAccountRepository) en interne
 * - Elle mappe entre Account (domaine) et AccountEntity (JPA) via AccountMapper
 *
 * @Repository est un stéréotype Spring (comme @Service, @Controller).
 * Il indique que cette classe est un bean de persistance.
 * Spring le détecte automatiquement et crée une instance (bean).
 *
 * Le flux pour un save() :
 * Account (domaine) → AccountMapper.toEntity() → AccountEntity (JPA)
 *                     → SpringDataAccountRepository.save() → SQL INSERT
 *
 * Le flux pour un findById() :
 * SQL SELECT → AccountEntity (JPA) → AccountMapper.toDomain() → Account (domaine)
 */
@Repository
public class JpaAccountRepositoryAdapter implements AccountRepository {

    private final SpringDataAccountRepository jpaRepository;

    public JpaAccountRepositoryAdapter(SpringDataAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        jpaRepository.save(AccountMapper.toEntity(account));
        return account;
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.value())
                .map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream()
                .map(AccountMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(AccountId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsById(AccountId id) {
        return jpaRepository.existsById(id.value());
    }
}
