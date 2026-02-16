package com.wealthwise.unit.domain.account;

import com.wealthwise.domain.account.model.*;
import com.wealthwise.domain.account.event.AccountCreated;
import com.wealthwise.domain.account.event.AccountBalanceUpdated;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests TDD pour l'Aggregate Root Account.
 *
 * Un Aggregate Root est le gardien des règles métier.
 * Toute modification passe par lui — on ne touche jamais directement
 * à Balance ou aux autres Value Objects depuis l'extérieur.
 *
 * L'Aggregate émet des Domain Events pour notifier le reste du système
 * de ce qui s'est passé, sans coupler les contextes entre eux.
 */
@DisplayName("Account - Aggregate Root")
class AccountTest {

    @Nested
    @DisplayName("Création")
    class Creation {

        @Test
        @DisplayName("doit créer un compte avec un solde initial à zéro")
        void shouldCreateAccountWithZeroBalance() {
            Account account = Account.create(
                    "Compte Courant",
                    AccountType.CHECKING,
                    Currency.EUR
            );

            assertThat(account.getId()).isNotNull();
            assertThat(account.getName()).isEqualTo("Compte Courant");
            assertThat(account.getType()).isEqualTo(AccountType.CHECKING);
            assertThat(account.getCurrency()).isEqualTo(Currency.EUR);
            assertThat(account.getBalance().money().isZero()).isTrue();
            assertThat(account.isClosed()).isFalse();
        }

        @Test
        @DisplayName("doit émettre un événement AccountCreated à la création")
        void shouldEmitAccountCreatedEvent() {
            Account account = Account.create(
                    "Livret A",
                    AccountType.SAVINGS,
                    Currency.EUR
            );

            assertThat(account.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(AccountCreated.class);

            AccountCreated event = (AccountCreated) account.getDomainEvents().get(0);
            assertThat(event.accountId()).isEqualTo(account.getId());
            assertThat(event.name()).isEqualTo("Livret A");
            assertThat(event.type()).isEqualTo(AccountType.SAVINGS);
        }

        @Test
        @DisplayName("doit rejeter un nom vide")
        void shouldRejectBlankName() {
            assertThatThrownBy(() -> Account.create("", AccountType.CHECKING, Currency.EUR))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");

            assertThatThrownBy(() -> Account.create(null, AccountType.CHECKING, Currency.EUR))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Crédit (déposer de l'argent)")
    class Credit {

        @Test
        @DisplayName("doit créditer le compte et mettre à jour le solde")
        void shouldCreditAccount() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);

            account.credit(Money.of(500, Currency.EUR));

            assertThat(account.getBalance().money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(500));
        }

        @Test
        @DisplayName("doit émettre un événement AccountBalanceUpdated")
        void shouldEmitBalanceUpdatedEventOnCredit() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
            account.clearEvents(); // Vider l'événement AccountCreated

            account.credit(Money.of(500, Currency.EUR));

            assertThat(account.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(AccountBalanceUpdated.class);
        }

        @Test
        @DisplayName("doit rejeter un crédit sur un compte fermé")
        void shouldRejectCreditOnClosedAccount() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
            account.close();

            assertThatThrownBy(() -> account.credit(Money.of(100, Currency.EUR)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("closed");
        }

        @Test
        @DisplayName("doit rejeter un crédit dans une devise différente")
        void shouldRejectCreditWithDifferentCurrency() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);

            assertThatThrownBy(() -> account.credit(Money.of(100, Currency.USD)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("currency");
        }
    }

    @Nested
    @DisplayName("Débit (retirer de l'argent)")
    class Debit {

        @Test
        @DisplayName("doit débiter le compte quand les fonds sont suffisants")
        void shouldDebitWhenSufficientFunds() {
            Account account = createAccountWithBalance(1000);

            account.debit(Money.of(300, Currency.EUR));

            assertThat(account.getBalance().money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(700));
        }

        @Test
        @DisplayName("doit rejeter un débit quand les fonds sont insuffisants")
        void shouldRejectDebitWhenInsufficientFunds() {
            Account account = createAccountWithBalance(100);

            assertThatThrownBy(() -> account.debit(Money.of(500, Currency.EUR)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Insufficient");
        }

        @Test
        @DisplayName("doit permettre un débit du montant exact du solde")
        void shouldAllowDebitOfExactBalance() {
            Account account = createAccountWithBalance(500);

            account.debit(Money.of(500, Currency.EUR));

            assertThat(account.getBalance().money().isZero()).isTrue();
        }

        @Test
        @DisplayName("doit rejeter un débit sur un compte fermé")
        void shouldRejectDebitOnClosedAccount() {
            // On crée un compte avec solde à zéro pour pouvoir le fermer
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
            account.close();

            assertThatThrownBy(() -> account.debit(Money.of(100, Currency.EUR)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("closed");
        }
    }

    @Nested
    @DisplayName("Fermeture de compte")
    class Closing {

        @Test
        @DisplayName("doit fermer un compte avec solde à zéro")
        void shouldCloseAccountWithZeroBalance() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);

            account.close();

            assertThat(account.isClosed()).isTrue();
        }

        @Test
        @DisplayName("doit rejeter la fermeture si le solde n'est pas à zéro")
        void shouldRejectCloseWithNonZeroBalance() {
            Account account = createAccountWithBalance(500);

            assertThatThrownBy(() -> account.close())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("balance");
        }

        @Test
        @DisplayName("doit rejeter une double fermeture")
        void shouldRejectDoubleClose() {
            Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
            account.close();

            assertThatThrownBy(() -> account.close())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already closed");
        }
    }

    // ========== Helper ==========

    private Account createAccountWithBalance(double amount) {
        Account account = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
        account.credit(Money.of(amount, Currency.EUR));
        account.clearEvents(); // On ne veut pas les events de setup dans les assertions
        return account;
    }
}