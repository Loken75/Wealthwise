package com.wealthwise.unit.domain.shared;

import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Money - Value Object")
class MoneyTest {

    @Nested
    @DisplayName("Création")
    class Creation {

        @Test
        @DisplayName("doit créer un Money avec un montant et une devise valides")
        void shouldCreateMoneyWithValidAmountAndCurrency() {
            Money money = Money.of(100.50, Currency.EUR);

            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.valueOf(100.50));
            assertThat(money.currency()).isEqualTo(Currency.EUR);
        }

        @Test
        @DisplayName("doit créer un Money à zéro")
        void shouldCreateZeroMoney() {
            Money money = Money.of(0, Currency.EUR);

            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("doit rejeter une devise null")
        void shouldRejectNullCurrency() {
            assertThatThrownBy(() -> Money.of(100, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("doit arrondir à 2 décimales")
        void shouldRoundToTwoDecimals() {
            Money money = Money.of(10.456, Currency.EUR);

            // 10.456 arrondi HALF_UP → 10.46
            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.valueOf(10.46));
        }

        @Test
        @DisplayName("doit créer un Money avec factory method zero()")
        void shouldCreateZeroWithFactory() {
            Money zero = Money.zero(Currency.EUR);

            assertThat(zero.amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(zero.currency()).isEqualTo(Currency.EUR);
        }
    }

    @Nested
    @DisplayName("Addition")
    class Addition {

        @Test
        @DisplayName("doit additionner deux Money de même devise")
        void shouldAddMoneyWithSameCurrency() {
            Money money1 = Money.of(100, Currency.EUR);
            Money money2 = Money.of(50.50, Currency.EUR);

            Money result = money1.add(money2);

            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(150.50));
            assertThat(result.currency()).isEqualTo(Currency.EUR);
        }

        @Test
        @DisplayName("ne doit pas modifier les Money originaux (immutabilité)")
        void shouldNotMutateOriginalMoney() {
            Money money1 = Money.of(100, Currency.EUR);
            Money money2 = Money.of(50, Currency.EUR);

            money1.add(money2);

            // money1 doit rester à 100 — il est immutable
            assertThat(money1.amount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("doit rejeter l'addition de devises différentes")
        void shouldRejectAdditionWithDifferentCurrencies() {
            Money euros = Money.of(100, Currency.EUR);
            Money dollars = Money.of(50, Currency.USD);

            assertThatThrownBy(() -> euros.add(dollars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("currency");
        }
    }

    @Nested
    @DisplayName("Soustraction")
    class Subtraction {

        @Test
        @DisplayName("doit soustraire deux Money de même devise")
        void shouldSubtractMoneyWithSameCurrency() {
            Money money1 = Money.of(100, Currency.EUR);
            Money money2 = Money.of(30.50, Currency.EUR);

            Money result = money1.subtract(money2);

            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(69.50));
        }

        @Test
        @DisplayName("doit permettre un résultat négatif (découvert)")
        void shouldAllowNegativeResult() {
            Money money1 = Money.of(50, Currency.EUR);
            Money money2 = Money.of(100, Currency.EUR);

            Money result = money1.subtract(money2);

            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(-50));
        }

        @Test
        @DisplayName("doit rejeter la soustraction de devises différentes")
        void shouldRejectSubtractionWithDifferentCurrencies() {
            Money euros = Money.of(100, Currency.EUR);
            Money dollars = Money.of(50, Currency.USD);

            assertThatThrownBy(() -> euros.subtract(dollars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("currency");
        }
    }

    @Nested
    @DisplayName("Négation")
    class Negation {

        @Test
        @DisplayName("doit inverser le signe d'un montant positif")
        void shouldNegatePositiveAmount() {
            Money money = Money.of(100, Currency.EUR);

            Money result = money.negate();

            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(-100));
        }

        @Test
        @DisplayName("doit inverser le signe d'un montant négatif")
        void shouldNegateNegativeAmount() {
            Money money = Money.of(100, Currency.EUR).negate();

            Money result = money.negate();

            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }
    }

    @Nested
    @DisplayName("Comparaisons")
    class Comparisons {

        @Test
        @DisplayName("doit détecter un montant positif")
        void shouldDetectPositiveAmount() {
            assertThat(Money.of(100, Currency.EUR).isPositive()).isTrue();
            assertThat(Money.of(0, Currency.EUR).isPositive()).isFalse();
        }

        @Test
        @DisplayName("doit détecter un montant négatif")
        void shouldDetectNegativeAmount() {
            assertThat(Money.of(100, Currency.EUR).negate().isNegative()).isTrue();
            assertThat(Money.of(0, Currency.EUR).isNegative()).isFalse();
        }

        @Test
        @DisplayName("doit détecter un montant à zéro")
        void shouldDetectZeroAmount() {
            assertThat(Money.of(0, Currency.EUR).isZero()).isTrue();
            assertThat(Money.of(100, Currency.EUR).isZero()).isFalse();
        }

        @Test
        @DisplayName("doit vérifier que deux Money sont supérieurs/inférieurs")
        void shouldCompareMoneyValues() {
            Money big = Money.of(200, Currency.EUR);
            Money small = Money.of(50, Currency.EUR);

            assertThat(big.isGreaterThan(small)).isTrue();
            assertThat(small.isGreaterThan(big)).isFalse();
        }
    }

    @Nested
    @DisplayName("Égalité (comportement Value Object)")
    class Equality {

        @Test
        @DisplayName("deux Money avec même montant et devise sont égaux")
        void shouldBeEqualWithSameAmountAndCurrency() {
            Money money1 = Money.of(100, Currency.EUR);
            Money money2 = Money.of(100, Currency.EUR);

            // Grâce au record, equals() compare les champs automatiquement
            assertThat(money1).isEqualTo(money2);
            assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        }

        @Test
        @DisplayName("deux Money avec des devises différentes ne sont pas égaux")
        void shouldNotBeEqualWithDifferentCurrency() {
            Money euros = Money.of(100, Currency.EUR);
            Money dollars = Money.of(100, Currency.USD);

            assertThat(euros).isNotEqualTo(dollars);
        }
    }
}
