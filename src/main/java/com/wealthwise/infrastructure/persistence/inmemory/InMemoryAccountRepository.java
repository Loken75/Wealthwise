package com.wealthwise.infrastructure.persistence.inmemory;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation en mémoire du AccountRepository pour le développement.
 *
 * TEMPORAIRE : sera remplacé par JpaAccountRepository quand on ajoutera PostgreSQL.
 *
 * Utilise ConcurrentHashMap (au lieu de HashMap) car Spring crée un seul bean
 * partagé entre tous les threads (requêtes HTTP). ConcurrentHashMap est thread-safe.
 *
 * ATTENTION : les données sont perdues à chaque redémarrage de l'application.
 * C'est voulu pour cette phase de développement.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> store = new ConcurrentHashMap<>();

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
}
