package com.wealthwise.application.command;

import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.port.TransactionRepository;

/**
 * Use Case : catégoriser une transaction existante.
 *
 * Vérifie que la transaction ET la catégorie existent avant de catégoriser.
 * La logique de catégorisation elle-même est dans le domaine (Transaction.categorize()).
 */
public class CategorizeTransactionCommandHandler {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public CategorizeTransactionCommandHandler(TransactionRepository transactionRepository,
                                               CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public void handle(CategorizeTransactionCommand command) {
        // 1. Trouver la transaction
        Transaction transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found: " + command.transactionId().value()
                ));

        // 2. Vérifier que la catégorie existe
        if (!categoryRepository.findById(command.categoryId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Category not found: " + command.categoryId().value()
            );
        }

        // 3. Catégoriser (le domaine valide)
        transaction.categorize(command.categoryId(), command.confidenceLevel());

        // 4. Persister
        transactionRepository.save(transaction);
    }
}
