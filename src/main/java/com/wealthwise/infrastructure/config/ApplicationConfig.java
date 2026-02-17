package com.wealthwise.infrastructure.config;

import com.wealthwise.application.command.*;
import com.wealthwise.domain.account.port.AccountRepository;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.domain.transaction.port.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring qui câble les Command Handlers.
 *
 * Les Repositories ne sont PLUS déclarés ici !
 * Les adaptateurs JPA (JpaAccountRepositoryAdapter, etc.) sont annotés @Repository,
 * donc Spring les détecte et les crée automatiquement (component scanning).
 *
 * Spring injecte automatiquement le bon adaptateur JPA quand un bean
 * a besoin d'un AccountRepository, TransactionRepository, etc.
 * C'est la puissance de l'injection de dépendance par interface.
 */
@Configuration
public class ApplicationConfig {

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
