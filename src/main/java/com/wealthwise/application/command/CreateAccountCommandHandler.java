package com.wealthwise.application.command;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;

/**
 * Use Case : créer un nouveau compte.
 *
 * Un Command Handler est la couche Application — il orchestre :
 * 1. Recevoir la commande (les données d'entrée)
 * 2. Appeler le domaine pour exécuter la logique métier
 * 3. Persister le résultat via le port (repository)
 * 4. Retourner le résultat
 *
 * IMPORTANT : le handler ne contient PAS de logique métier.
 * La validation et les règles sont dans le domaine (Account.create()).
 * Le handler est un simple chef d'orchestre.
 *
 * Il dépend de l'INTERFACE AccountRepository (le port),
 * pas d'une implémentation concrète. C'est l'injection de dépendance
 * qui fournira la bonne implémentation au runtime.
 */
public class CreateAccountCommandHandler {

    private final AccountRepository accountRepository;

    /**
     * Injection de dépendance par constructeur.
     *
     * On injecte l'interface, pas l'implémentation.
     * En test, on injectera un mock (un faux repository en mémoire).
     * En production, Spring injectera le JpaAccountRepository.
     */
    public CreateAccountCommandHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Exécute la commande de création de compte.
     *
     * @param command les données du compte à créer
     * @return l'identifiant du compte créé
     */
    public AccountId handle(CreateAccountCommand command) {
        // 1. Déléguer la création au domaine (c'est lui qui valide)
        Account account = Account.create(
                command.name(),
                command.type(),
                command.currency()
        );

        // 2. Persister via le port
        accountRepository.save(account);

        // 3. Retourner l'identifiant du compte créé
        return account.getId();
    }
}
