package com.wealthwise.unit.domain.transaction;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.event.TransactionCategorized;
import com.wealthwise.domain.transaction.event.TransactionCreated;
import com.wealthwise.domain.transaction.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests TDD pour l'Aggregate Root Transaction.
 *
 * La Transaction est le coeur de l'application : chaque opération
 * financière (salaire, courses, loyer...) est une Transaction.
 */
@DisplayName("Transaction - Aggregate Root")
class TransactionTest {

    // Constantes pour éviter la répétition dans les tests
    private static final AccountId ACCOUNT_ID = AccountId.of("test-account-1");
    private static final Money HUNDRED_EUR = Money.of(100, Currency.EUR);
    private static final LocalDate TODAY = LocalDate.now();

    @Nested
    @DisplayName("Création")
    class Creation {

        @Test
        @DisplayName("doit créer une dépense avec toutes les infos")
        void shouldCreateExpense() {
            Transaction tx = Transaction.create(
                    ACCOUNT_ID,
                    Money.of(42.50, Currency.EUR),
                    "Courses Carrefour",
                    TODAY,
                    TransactionType.EXPENSE
            );

            assertThat(tx.getId()).isNotNull();
            assertThat(tx.getAccountId()).isEqualTo(ACCOUNT_ID);
            assertThat(tx.getAmount().amount()).isEqualByComparingTo(BigDecimal.valueOf(42.50));
            assertThat(tx.getDescription()).isEqualTo("Courses Carrefour");
            assertThat(tx.getDate()).isEqualTo(TODAY);
            assertThat(tx.getType()).isEqualTo(TransactionType.EXPENSE);
            assertThat(tx.isExpense()).isTrue();
            assertThat(tx.isIncome()).isFalse();
            assertThat(tx.isCategorized()).isFalse();
        }

        @Test
        @DisplayName("doit créer un revenu")
        void shouldCreateIncome() {
            Transaction tx = Transaction.create(
                    ACCOUNT_ID,
                    Money.of(2500, Currency.EUR),
                    "Salaire Février",
                    TODAY,
                    TransactionType.INCOME
            );

            assertThat(tx.isIncome()).isTrue();
            assertThat(tx.isExpense()).isFalse();
        }

        @Test
        @DisplayName("doit émettre un événement TransactionCreated")
        void shouldEmitTransactionCreatedEvent() {
            Transaction tx = Transaction.create(
                    ACCOUNT_ID, HUNDRED_EUR, "Test", TODAY, TransactionType.EXPENSE
            );

            assertThat(tx.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(TransactionCreated.class);

            TransactionCreated event = (TransactionCreated) tx.getDomainEvents().get(0);
            assertThat(event.transactionId()).isEqualTo(tx.getId());
            assertThat(event.accountId()).isEqualTo(ACCOUNT_ID);
            assertThat(event.amount()).isEqualTo(HUNDRED_EUR);
            assertThat(event.type()).isEqualTo(TransactionType.EXPENSE);
        }

        @Test
        @DisplayName("doit rejeter un montant négatif ou zéro")
        void shouldRejectNonPositiveAmount() {
            Money zero = Money.of(0, Currency.EUR);
            Money negative = Money.of(100, Currency.EUR).negate();

            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, zero, "Test", TODAY, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");

            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, negative, "Test", TODAY, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("doit rejeter une description vide")
        void shouldRejectBlankDescription() {
            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, HUNDRED_EUR, "", TODAY, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("description");

            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, HUNDRED_EUR, null, TODAY, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("doit rejeter un AccountId null")
        void shouldRejectNullAccountId() {
            assertThatThrownBy(() -> Transaction.create(
                    null, HUNDRED_EUR, "Test", TODAY, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("AccountId");
        }

        @Test
        @DisplayName("doit rejeter une date null")
        void shouldRejectNullDate() {
            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, HUNDRED_EUR, "Test", null, TransactionType.EXPENSE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("date");
        }

        @Test
        @DisplayName("doit rejeter un type null")
        void shouldRejectNullType() {
            assertThatThrownBy(() -> Transaction.create(
                    ACCOUNT_ID, HUNDRED_EUR, "Test", TODAY, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("type");
        }
    }

    @Nested
    @DisplayName("Catégorisation")
    class Categorization {

        @Test
        @DisplayName("doit catégoriser manuellement une transaction")
        void shouldCategorizeManually() {
            Transaction tx = createExpense();
            CategoryId foodCategory = CategoryId.of("cat-food");

            tx.categorize(foodCategory, ConfidenceLevel.MANUAL);

            assertThat(tx.isCategorized()).isTrue();
            assertThat(tx.getCategoryId()).isEqualTo(foodCategory);
            assertThat(tx.getConfidenceLevel()).isEqualTo(ConfidenceLevel.MANUAL);
        }

        @Test
        @DisplayName("doit catégoriser automatiquement avec un niveau de confiance")
        void shouldCategorizeAutomatically() {
            Transaction tx = createExpense();
            CategoryId foodCategory = CategoryId.of("cat-food");

            tx.categorize(foodCategory, ConfidenceLevel.HIGH);

            assertThat(tx.getConfidenceLevel()).isEqualTo(ConfidenceLevel.HIGH);
        }

        @Test
        @DisplayName("doit émettre un événement TransactionCategorized")
        void shouldEmitTransactionCategorizedEvent() {
            Transaction tx = createExpense();
            tx.clearEvents(); // Vider le TransactionCreated
            CategoryId foodCategory = CategoryId.of("cat-food");

            tx.categorize(foodCategory, ConfidenceLevel.MANUAL);

            assertThat(tx.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(TransactionCategorized.class);

            TransactionCategorized event = (TransactionCategorized) tx.getDomainEvents().get(0);
            assertThat(event.transactionId()).isEqualTo(tx.getId());
            assertThat(event.categoryId()).isEqualTo(foodCategory);
            assertThat(event.confidence()).isEqualTo(ConfidenceLevel.MANUAL);
        }

        @Test
        @DisplayName("doit permettre de re-catégoriser une transaction")
        void shouldAllowRecategorization() {
            Transaction tx = createExpense();
            CategoryId foodCategory = CategoryId.of("cat-food");
            CategoryId transportCategory = CategoryId.of("cat-transport");

            tx.categorize(foodCategory, ConfidenceLevel.LOW);
            tx.categorize(transportCategory, ConfidenceLevel.MANUAL);

            assertThat(tx.getCategoryId()).isEqualTo(transportCategory);
            assertThat(tx.getConfidenceLevel()).isEqualTo(ConfidenceLevel.MANUAL);
        }

        @Test
        @DisplayName("doit rejeter une catégorie null")
        void shouldRejectNullCategory() {
            Transaction tx = createExpense();

            assertThatThrownBy(() -> tx.categorize(null, ConfidenceLevel.MANUAL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CategoryId");
        }

        @Test
        @DisplayName("doit rejeter un niveau de confiance null")
        void shouldRejectNullConfidenceLevel() {
            Transaction tx = createExpense();
            CategoryId foodCategory = CategoryId.of("cat-food");

            assertThatThrownBy(() -> tx.categorize(foodCategory, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ConfidenceLevel");
        }
    }

    // ========== Helper ==========

    private Transaction createExpense() {
        return Transaction.create(
                ACCOUNT_ID, HUNDRED_EUR, "Test expense", TODAY, TransactionType.EXPENSE
        );
    }
}
