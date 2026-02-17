package com.wealthwise.unit.application;

import com.wealthwise.application.command.CategorizeTransactionCommand;
import com.wealthwise.application.command.CategorizeTransactionCommandHandler;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.Category;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.category.model.CategoryType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.model.ConfidenceLevel;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionType;
import com.wealthwise.unit.infrastructure.fake.InMemoryCategoryRepository;
import com.wealthwise.unit.infrastructure.fake.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Use Case : CategorizeTransaction")
class CategorizeTransactionCommandHandlerTest {

    private InMemoryTransactionRepository transactionRepository;
    private InMemoryCategoryRepository categoryRepository;
    private CategorizeTransactionCommandHandler handler;

    private Transaction testTransaction;
    private Category foodCategory;

    @BeforeEach
    void setUp() {
        transactionRepository = new InMemoryTransactionRepository();
        categoryRepository = new InMemoryCategoryRepository();
        handler = new CategorizeTransactionCommandHandler(transactionRepository, categoryRepository);

        // Créer une transaction de test
        testTransaction = Transaction.create(
                AccountId.of("acc-1"), Money.of(42, Currency.EUR),
                "Courses", LocalDate.now(), TransactionType.EXPENSE
        );
        transactionRepository.save(testTransaction);

        // Créer une catégorie de test
        foodCategory = Category.create("Alimentation", CategoryType.EXPENSE, "#FF5733", "cart");
        categoryRepository.save(foodCategory);
    }

    @Test
    @DisplayName("doit catégoriser une transaction existante")
    void shouldCategorizeTransaction() {
        CategorizeTransactionCommand command = new CategorizeTransactionCommand(
                testTransaction.getId(), foodCategory.getId(), ConfidenceLevel.MANUAL
        );

        handler.handle(command);

        Transaction updated = transactionRepository.findById(testTransaction.getId()).get();
        assertThat(updated.isCategorized()).isTrue();
        assertThat(updated.getCategoryId()).isEqualTo(foodCategory.getId());
        assertThat(updated.getConfidenceLevel()).isEqualTo(ConfidenceLevel.MANUAL);
    }

    @Test
    @DisplayName("doit rejeter si la transaction n'existe pas")
    void shouldRejectUnknownTransaction() {
        CategorizeTransactionCommand command = new CategorizeTransactionCommand(
                com.wealthwise.domain.transaction.model.TransactionId.of("unknown"),
                foodCategory.getId(), ConfidenceLevel.MANUAL
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction not found");
    }

    @Test
    @DisplayName("doit rejeter si la catégorie n'existe pas")
    void shouldRejectUnknownCategory() {
        CategorizeTransactionCommand command = new CategorizeTransactionCommand(
                testTransaction.getId(), CategoryId.of("unknown"), ConfidenceLevel.MANUAL
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
    }
}
