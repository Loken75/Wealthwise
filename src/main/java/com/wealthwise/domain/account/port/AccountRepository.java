package com.wealthwise.domain.account.port;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des comptes.
 *
 * C'est une INTERFACE dans la couche domaine. Elle définit CE DONT
 * le domaine a besoin, sans dire COMMENT c'est fait.
 *
 * En architecture hexagonale, c'est un "port de sortie" (driven port) :
 * - Le domaine DÉFINIT l'interface
 * - L'infrastructure IMPLÉMENTE l'interface (JPA, MongoDB, fichier...)
 *
 * Le domaine ne sait pas (et ne veut pas savoir) si les données
 * sont dans PostgreSQL, MongoDB ou un fichier CSV. Il sait juste
 * qu'il peut save() et findById().
 *
 * On n'implémente PAS cette interface maintenant — on le fera
 * dans la Phase 3 (Infrastructure) avec JPA/PostgreSQL.
 */
public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    List<Account> findAll();

    void deleteById(AccountId id);

    boolean existsById(AccountId id);
}
