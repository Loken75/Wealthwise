package com.wealthwise.unit.infrastructure.fake;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation en mémoire du AccountRepository pour les tests.
 *
 * C'est un "Fake" — un vrai objet fonctionnel, mais simplifié.
 * Au lieu d'écrire en base de données, il stocke dans une HashMap.
 *
 * Avantages par rapport à un mock (Mockito) :
 * - Plus réaliste : les données persistent entre les appels
 * - Plus lisible : pas de when().thenReturn() à configurer
 * - Plus maintenable : un seul fake pour tous les tests
 *
 * Ce fake est dans src/test, il n'existe PAS en production.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> store = new HashMap<>();

    @Override
    public Account save(Account account) {
        store.put(account.getId().value(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(AccountId id) {
        store.remove(id.value());
    }

    @Override
    public boolean existsById(AccountId id) {
        return store.containsKey(id.value());
    }

    // Méthode utilitaire pour les tests
    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
