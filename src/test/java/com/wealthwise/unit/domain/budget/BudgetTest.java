package com.wealthwise.unit.domain.budget;

import com.wealthwise.domain.budget.event.BudgetExceeded;
import com.wealthwise.domain.budget.event.BudgetWarningReached;
import com.wealthwise.domain.budget.model.*;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests TDD pour l'Aggregate Root Budget.
 *
 * Le Budget est l'agrégat le plus riche en logique métier :
 * il suit les dépenses, calcule les pourcentages, change de statut
 * et émet des événements quand des seuils sont franchis.
 */
@DisplayName("Budget - Aggregate Root")
class BudgetTest {

    private static final CategoryId FOOD_CATEGORY = CategoryId.of("cat-food");
    private static final BudgetPeriod FEB_2026 = BudgetPeriod.of(YearMonth.of(2026, 2));

    @Nested
    @DisplayName("Création")
    class Creation {

        @Test
        @DisplayName("doit créer un budget avec statut ON_TRACK et zéro dépensé")
        void shouldCreateBudgetOnTrack() {
            Budget budget = Budget.create(
                    FOOD_CATEGORY,
                    Money.of(500, Currency.EUR),
                    FEB_2026
            );

            assertThat(budget.getId()).isNotNull();
            assertThat(budget.getCategoryId()).isEqualTo(FOOD_CATEGORY);
            assertThat(budget.getLimit().amount()).isEqualByComparingTo("500.00");
            assertThat(budget.getSpent().isZero()).isTrue();
            assertThat(budget.getStatus()).isEqualTo(BudgetStatus.ON_TRACK);
            assertThat(budget.getPeriod()).isEqualTo(FEB_2026);
        }

        @Test
        @DisplayName("doit rejeter une limite négative ou zéro")
        void shouldRejectNonPositiveLimit() {
            assertThatThrownBy(() -> Budget.create(
                    FOOD_CATEGORY, Money.of(0, Currency.EUR), FEB_2026))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");

            assertThatThrownBy(() -> Budget.create(
                    FOOD_CATEGORY, Money.of(100, Currency.EUR).negate(), FEB_2026))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("doit rejeter des paramètres null")
        void shouldRejectNullParameters() {
            assertThatThrownBy(() -> Budget.create(
                    null, Money.of(500, Currency.EUR), FEB_2026))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CategoryId");

            assertThatThrownBy(() -> Budget.create(
                    FOOD_CATEGORY, null, FEB_2026))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> Budget.create(
                    FOOD_CATEGORY, Money.of(500, Currency.EUR), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("BudgetPeriod");
        }
    }

    @Nested
    @DisplayName("Enregistrement de dépenses")
    class RecordExpense {

        @Test
        @DisplayName("doit enregistrer une dépense et mettre à jour le montant dépensé")
        void shouldRecordExpense() {
            Budget budget = createBudget(500);

            budget.recordExpense(Money.of(100, Currency.EUR));

            assertThat(budget.getSpent().amount()).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("doit cumuler plusieurs dépenses")
        void shouldAccumulateExpenses() {
            Budget budget = createBudget(500);

            budget.recordExpense(Money.of(100, Currency.EUR));
            budget.recordExpense(Money.of(50, Currency.EUR));
            budget.recordExpense(Money.of(75, Currency.EUR));

            assertThat(budget.getSpent().amount()).isEqualByComparingTo("225.00");
        }

        @Test
        @DisplayName("doit rejeter une dépense négative")
        void shouldRejectNegativeExpense() {
            Budget budget = createBudget(500);

            assertThatThrownBy(() -> budget.recordExpense(Money.of(100, Currency.EUR).negate()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("doit rejeter une dépense dans une devise différente")
        void shouldRejectDifferentCurrency() {
            Budget budget = createBudget(500);

            assertThatThrownBy(() -> budget.recordExpense(Money.of(100, Currency.USD)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("currency");
        }
    }

    @Nested
    @DisplayName("Calculs")
    class Calculations {

        @Test
        @DisplayName("doit calculer le montant restant")
        void shouldCalculateRemainingAmount() {
            Budget budget = createBudget(500);
            budget.recordExpense(Money.of(200, Currency.EUR));

            Money remaining = budget.getRemainingAmount();

            assertThat(remaining.amount()).isEqualByComparingTo("300.00");
        }

        @Test
        @DisplayName("doit avoir un montant restant négatif si dépassé")
        void shouldHaveNegativeRemainingWhenExceeded() {
            Budget budget = createBudget(500);
            budget.recordExpense(Money.of(600, Currency.EUR));

            Money remaining = budget.getRemainingAmount();

            assertThat(remaining.isNegative()).isTrue();
            assertThat(remaining.amount()).isEqualByComparingTo("-100.00");
        }

        @Test
        @DisplayName("doit calculer le pourcentage d'utilisation")
        void shouldCalculateUsagePercentage() {
            Budget budget = createBudget(200);
            budget.recordExpense(Money.of(100, Currency.EUR));

            assertThat(budget.getUsagePercentage()).isCloseTo(0.50, within(0.01));
        }
    }

    @Nested
    @DisplayName("Changement de statut et événements")
    class StatusAndEvents {

        @Test
        @DisplayName("doit rester ON_TRACK sous 80%")
        void shouldStayOnTrackBelow80Percent() {
            Budget budget = createBudget(100);
            budget.recordExpense(Money.of(79, Currency.EUR)); // 79%

            assertThat(budget.getStatus()).isEqualTo(BudgetStatus.ON_TRACK);
            assertThat(budget.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("doit passer en WARNING à 80%")
        void shouldWarnAt80Percent() {
            Budget budget = createBudget(100);
            budget.recordExpense(Money.of(80, Currency.EUR)); // 80%

            assertThat(budget.getStatus()).isEqualTo(BudgetStatus.WARNING);
            assertThat(budget.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(BudgetWarningReached.class);
        }

        @Test
        @DisplayName("doit passer en EXCEEDED au-dessus de 100%")
        void shouldExceedAbove100Percent() {
            Budget budget = createBudget(100);
            budget.recordExpense(Money.of(101, Currency.EUR)); // 101%

            assertThat(budget.getStatus()).isEqualTo(BudgetStatus.EXCEEDED);
            assertThat(budget.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(BudgetExceeded.class);

            BudgetExceeded event = (BudgetExceeded) budget.getDomainEvents().get(0);
            assertThat(event.budgetId()).isEqualTo(budget.getId());
            assertThat(event.limit().amount()).isEqualByComparingTo("100.00");
            assertThat(event.spent().amount()).isEqualByComparingTo("101.00");
        }

        @Test
        @DisplayName("ne doit pas émettre deux fois le même événement de statut")
        void shouldNotEmitDuplicateStatusEvents() {
            Budget budget = createBudget(100);

            // Passer en WARNING
            budget.recordExpense(Money.of(85, Currency.EUR)); // 85%
            assertThat(budget.getDomainEvents()).hasSize(1);

            // Rester en WARNING (pas de nouvel événement)
            budget.recordExpense(Money.of(10, Currency.EUR)); // 95%
            assertThat(budget.getDomainEvents()).hasSize(1); // toujours 1, pas 2
        }

        @Test
        @DisplayName("doit émettre WARNING puis EXCEEDED quand les seuils sont franchis progressivement")
        void shouldEmitWarningThenExceeded() {
            Budget budget = createBudget(100);

            budget.recordExpense(Money.of(85, Currency.EUR)); // 85% → WARNING
            budget.recordExpense(Money.of(20, Currency.EUR)); // 105% → EXCEEDED

            assertThat(budget.getDomainEvents()).hasSize(2);
            assertThat(budget.getDomainEvents().get(0)).isInstanceOf(BudgetWarningReached.class);
            assertThat(budget.getDomainEvents().get(1)).isInstanceOf(BudgetExceeded.class);
        }
    }

    @Nested
    @DisplayName("BudgetPeriod")
    class BudgetPeriodTest {

        @Test
        @DisplayName("doit vérifier qu'une date est dans la période")
        void shouldCheckDateInPeriod() {
            BudgetPeriod feb2026 = BudgetPeriod.of(YearMonth.of(2026, 2));

            assertThat(feb2026.contains(LocalDate.of(2026, 2, 1))).isTrue();
            assertThat(feb2026.contains(LocalDate.of(2026, 2, 15))).isTrue();
            assertThat(feb2026.contains(LocalDate.of(2026, 2, 28))).isTrue();
            assertThat(feb2026.contains(LocalDate.of(2026, 1, 31))).isFalse();
            assertThat(feb2026.contains(LocalDate.of(2026, 3, 1))).isFalse();
        }

        @Test
        @DisplayName("doit retourner les dates de début et fin")
        void shouldReturnStartAndEndDates() {
            BudgetPeriod feb2026 = BudgetPeriod.of(YearMonth.of(2026, 2));

            assertThat(feb2026.startDate()).isEqualTo(LocalDate.of(2026, 2, 1));
            assertThat(feb2026.endDate()).isEqualTo(LocalDate.of(2026, 2, 28));
        }
    }

    // ========== Helper ==========

    private Budget createBudget(double limitAmount) {
        return Budget.create(FOOD_CATEGORY, Money.of(limitAmount, Currency.EUR), FEB_2026);
    }
}
