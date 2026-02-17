package com.wealthwise.infrastructure.config;

import com.wealthwise.application.command.*;
import com.wealthwise.domain.account.port.AccountRepository;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.domain.transaction.port.TransactionRepository;
import com.wealthwise.infrastructure.persistence.inmemory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring qui câble l'application.
 *
 * @Configuration indique à Spring que cette classe contient des définitions de beans.
 * Un "bean" est un objet géré par Spring : Spring le crée, le stocke, et l'injecte
 * là où c'est nécessaire (injection de dépendance).
 *
 * @Bean sur une méthode dit à Spring : "appelle cette méthode au démarrage,
 * stocke le résultat, et injecte-le quand quelqu'un en a besoin".
 *
 * C'est ici que l'architecture hexagonale prend vie :
 * - On DÉCIDE quelle implémentation utiliser pour chaque port
 * - Pour l'instant : InMemory (pas de base de données)
 * - Plus tard : on remplacera par JPA sans toucher au domaine ni aux handlers
 */
@Configuration
public class ApplicationConfig {

    // ========== Repositories (adaptateurs de sortie) ==========
    // Pour l'instant en mémoire. Quand on ajoutera JPA,
    // on remplacera ces beans sans modifier une seule ligne
    // dans le domaine ou la couche application.

    @Bean
    public AccountRepository accountRepository() {
        return new InMemoryAccountRepository();
    }

    @Bean
    public TransactionRepository transactionRepository() {
        return new InMemoryTransactionRepository();
    }

    @Bean
    public CategoryRepository categoryRepository() {
        return new InMemoryCategoryRepository();
    }

    @Bean
    public BudgetRepository budgetRepository() {
        return new InMemoryBudgetRepository();
    }

    // ========== Command Handlers (use cases) ==========
    // Spring injecte automatiquement les repositories déclarés ci-dessus
    // dans les paramètres des méthodes @Bean.

    @Bean
    public CreateAccountCommandHandler createAccountCommandHandler(AccountRepository accountRepository) {
        return new CreateAccountCommandHandler(accountRepository);
    }

    @Bean
    public CreateTransactionCommandHandler createTransactionCommandHandler(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        return new CreateTransactionCommandHandler(transactionRepository, accountRepository);
    }

    @Bean
    public CategorizeTransactionCommandHandler categorizeTransactionCommandHandler(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository) {
        return new CategorizeTransactionCommandHandler(transactionRepository, categoryRepository);
    }

    @Bean
    public CreateBudgetCommandHandler createBudgetCommandHandler(
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository) {
        return new CreateBudgetCommandHandler(budgetRepository, categoryRepository);
    }
}
