package com.wealthwise.application.command;

import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.model.BudgetPeriod;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.port.CategoryRepository;
import com.wealthwise.domain.shared.Money;

/**
 * Use Case : créer un budget mensuel pour une catégorie.
 *
 * Vérifie que :
 * - La catégorie existe
 * - Il n'y a pas déjà un budget pour cette catégorie et cette période
 *   (on ne veut pas deux budgets "Alimentation" pour le même mois)
 */
public class CreateBudgetCommandHandler {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public CreateBudgetCommandHandler(BudgetRepository budgetRepository,
                                      CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public BudgetId handle(CreateBudgetCommand command) {
        // 1. Vérifier que la catégorie existe
        if (!categoryRepository.findById(command.categoryId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Category not found: " + command.categoryId().value()
            );
        }

        // 2. Vérifier qu'il n'y a pas de doublon
        BudgetPeriod period = BudgetPeriod.of(command.month());
        budgetRepository.findByCategoryIdAndPeriod(command.categoryId(), period)
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "A budget already exists for category %s and period %s"
                                    .formatted(command.categoryId().value(), command.month())
                    );
                });

        // 3. Créer le budget (le domaine valide la limite)
        Money limit = Money.of(command.limitAmount(), command.currency());
        Budget budget = Budget.create(command.categoryId(), limit, period);

        // 4. Persister
        budgetRepository.save(budget);

        return budget.getId();
    }
}
