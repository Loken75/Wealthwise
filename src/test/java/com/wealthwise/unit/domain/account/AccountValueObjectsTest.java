package com.wealthwise.unit.domain.account;

import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.account.model.Balance;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account - Value Objects")
class AccountValueObjectsTest {

    // ================================================================
    // AccountId : identifiant unique d'un compte
    // ================================================================
    @Nested
    @DisplayName("AccountId")
    class AccountIdTest {

        @Test
        @DisplayName("doit générer un identifiant unique")
        void shouldGenerateUniqueId() {
            AccountId id1 = AccountId.generate();
            AccountId id2 = AccountId.generate();

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("doit créer un AccountId à partir d'une valeur existante")
        void shouldCreateFromExistingValue() {
            AccountId id = AccountId.of("ACC-12345");

            assertThat(id.value()).isEqualTo("ACC-12345");
        }

        @Test
        @DisplayName("doit rejeter une valeur vide ou null")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> AccountId.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> AccountId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> AccountId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ================================================================
    // AccountType : type de compte (courant, épargne, etc.)
    // ================================================================
    @Nested
    @DisplayName("AccountType")
    class AccountTypeTest {

        @Test
        @DisplayName("doit avoir les types CHECKING, SAVINGS, CREDIT_CARD, CASH")
        void shouldHaveExpectedTypes() {
            // values() retourne tous les éléments de l'enum
            assertThat(AccountType.values()).containsExactly(
                    AccountType.CHECKING,
                    AccountType.SAVINGS,
                    AccountType.CREDIT_CARD,
                    AccountType.CASH
            );
        }
    }

    // ================================================================
    // Balance : solde d'un compte, encapsule Money + règles métier
    // ================================================================
    @Nested
    @DisplayName("Balance")
    class BalanceTest {

        @Test
        @DisplayName("doit créer un solde initial")
        void shouldCreateInitialBalance() {
            Balance balance = Balance.of(1000, Currency.EUR);

            assertThat(balance.money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("doit créer un solde à zéro")
        void shouldCreateZeroBalance() {
            Balance balance = Balance.zero(Currency.EUR);

            assertThat(balance.money().isZero()).isTrue();
        }

        @Test
        @DisplayName("doit créditer le solde (ajouter)")
        void shouldCreditBalance() {
            Balance balance = Balance.of(1000, Currency.EUR);
            Money deposit = Money.of(500, Currency.EUR);

            Balance result = balance.credit(deposit);

            assertThat(result.money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(1500));
        }

        @Test
        @DisplayName("doit débiter le solde (soustraire)")
        void shouldDebitBalance() {
            Balance balance = Balance.of(1000, Currency.EUR);
            Money withdrawal = Money.of(300, Currency.EUR);

            Balance result = balance.debit(withdrawal);

            assertThat(result.money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(700));
        }

        @Test
        @DisplayName("doit indiquer si un débit est possible (fonds suffisants)")
        void shouldCheckSufficientFunds() {
            Balance balance = Balance.of(1000, Currency.EUR);

            assertThat(balance.canDebit(Money.of(500, Currency.EUR))).isTrue();
            assertThat(balance.canDebit(Money.of(1000, Currency.EUR))).isTrue();
            assertThat(balance.canDebit(Money.of(1500, Currency.EUR))).isFalse();
        }

        @Test
        @DisplayName("doit rejeter un débit avec un montant négatif")
        void shouldRejectDebitWithNegativeAmount() {
            Balance balance = Balance.of(1000, Currency.EUR);
            Money negative = Money.of(100, Currency.EUR).negate();

            assertThatThrownBy(() -> balance.debit(negative))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("doit rejeter un crédit avec un montant négatif")
        void shouldRejectCreditWithNegativeAmount() {
            Balance balance = Balance.of(1000, Currency.EUR);
            Money negative = Money.of(100, Currency.EUR).negate();

            assertThatThrownBy(() -> balance.credit(negative))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }
    }
}
