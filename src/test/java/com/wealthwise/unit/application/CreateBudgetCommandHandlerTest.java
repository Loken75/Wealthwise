package com.wealthwise.unit.application;

import com.wealthwise.application.command.CreateBudgetCommand;
import com.wealthwise.application.command.CreateBudgetCommandHandler;
import com.wealthwise.domain.budget.model.Budget;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.model.BudgetPeriod;
import com.wealthwise.domain.budget.model.BudgetStatus;
import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.model.CategoryType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.unit.infrastructure.fake.InMemoryBudgetRepository;
import com.wealthwise.unit.infrastructure.fake.InMemoryCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Use Case : CreateBudget")
class CreateBudgetCommandHandlerTest {

    private InMemoryBudgetRepository budgetRepository;
    private InMemoryCategoryRepository categoryRepository;
    private CreateBudgetCommandHandler handler;

    private Category foodCategory;

    @BeforeEach
    void setUp() {
        budgetRepository = new InMemoryBudgetRepository();
        categoryRepository = new InMemoryCategoryRepository();
        handler = new CreateBudgetCommandHandler(budgetRepository, categoryRepository);

        // Créer une catégorie de test
        foodCategory = Category.create("Alimentation", CategoryType.EXPENSE, "#FF5733", "cart");
        categoryRepository.save(foodCategory);
    }

    @Test
    @DisplayName("doit créer un budget et le persister")
    void shouldCreateAndPersistBudget() {
        CreateBudgetCommand command = new CreateBudgetCommand(
                foodCategory.getId(), 500, Currency.EUR, YearMonth.of(2026, 2)
        );

        BudgetId id = handler.handle(command);

        assertThat(id).isNotNull();
        assertThat(budgetRepository.count()).isEqualTo(1);

        Budget saved = budgetRepository.findById(id).get();
        assertThat(saved.getCategoryId()).isEqualTo(foodCategory.getId());
        assertThat(saved.getLimit().amount()).isEqualByComparingTo("500.00");
        assertThat(saved.getStatus()).isEqualTo(BudgetStatus.ON_TRACK);
    }

    @Test
    @DisplayName("doit rejeter si la catégorie n'existe pas")
    void shouldRejectUnknownCategory() {
        CreateBudgetCommand command = new CreateBudgetCommand(
                CategoryId.of("unknown"), 500, Currency.EUR, YearMonth.of(2026, 2)
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");

        assertThat(budgetRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("doit rejeter un doublon catégorie + période")
    void shouldRejectDuplicateBudget() {
        YearMonth february = YearMonth.of(2026, 2);

        // Créer un premier budget
        handler.handle(new CreateBudgetCommand(
                foodCategory.getId(), 500, Currency.EUR, february
        ));

        // Tenter de créer un deuxième pour la même catégorie et période
        assertThatThrownBy(() -> handler.handle(new CreateBudgetCommand(
                foodCategory.getId(), 300, Currency.EUR, february
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        assertThat(budgetRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("doit permettre la même catégorie sur des mois différents")
    void shouldAllowSameCategoryDifferentMonths() {
        handler.handle(new CreateBudgetCommand(
                foodCategory.getId(), 500, Currency.EUR, YearMonth.of(2026, 2)
        ));
        handler.handle(new CreateBudgetCommand(
                foodCategory.getId(), 500, Currency.EUR, YearMonth.of(2026, 3)
        ));

        assertThat(budgetRepository.count()).isEqualTo(2);
    }
}
