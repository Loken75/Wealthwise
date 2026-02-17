package com.wealthwise.unit.application;

import com.wealthwise.application.command.CreateTransactionCommand;
import com.wealthwise.application.command.CreateTransactionCommandHandler;
import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.model.TransactionType;
import com.wealthwise.unit.infrastructure.fake.InMemoryAccountRepository;
import com.wealthwise.unit.infrastructure.fake.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests du Use Case CreateTransaction.
 *
 * Ce test vérifie l'orchestration entre Transaction et Account :
 * - Un INCOME crédite le compte
 * - Un EXPENSE débite le compte
 * - Un compte inexistant lève une erreur
 * - Les fonds insuffisants lèvent une erreur
 */
@DisplayName("Use Case : CreateTransaction")
class CreateTransactionCommandHandlerTest {

    private InMemoryAccountRepository accountRepository;
    private InMemoryTransactionRepository transactionRepository;
    private CreateTransactionCommandHandler handler;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        transactionRepository = new InMemoryTransactionRepository();
        handler = new CreateTransactionCommandHandler(transactionRepository, accountRepository);

        // Créer un compte de test avec 1000€
        testAccount = Account.create("Courant", AccountType.CHECKING, Currency.EUR);
        testAccount.credit(Money.of(1000, Currency.EUR));
        testAccount.clearEvents();
        accountRepository.save(testAccount);
    }

    @Nested
    @DisplayName("Dépense (EXPENSE)")
    class Expense {

        @Test
        @DisplayName("doit créer une dépense et débiter le compte")
        void shouldCreateExpenseAndDebitAccount() {
            CreateTransactionCommand command = new CreateTransactionCommand(
                    testAccount.getId(), 42.50, Currency.EUR,
                    "Courses Carrefour", LocalDate.now(), TransactionType.EXPENSE
            );

            TransactionId txId = handler.handle(command);

            // Transaction créée
            assertThat(txId).isNotNull();
            assertThat(transactionRepository.count()).isEqualTo(1);

            // Compte débité : 1000 - 42.50 = 957.50
            Account updated = accountRepository.findById(testAccount.getId()).get();
            assertThat(updated.getBalance().money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(957.50));
        }

        @Test
        @DisplayName("doit rejeter si fonds insuffisants")
        void shouldRejectInsufficientFunds() {
            CreateTransactionCommand command = new CreateTransactionCommand(
                    testAccount.getId(), 5000, Currency.EUR,
                    "Grosse dépense", LocalDate.now(), TransactionType.EXPENSE
            );

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Insufficient");

            // Rien n'a été persisté
            assertThat(transactionRepository.count()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Revenu (INCOME)")
    class Income {

        @Test
        @DisplayName("doit créer un revenu et créditer le compte")
        void shouldCreateIncomeAndCreditAccount() {
            CreateTransactionCommand command = new CreateTransactionCommand(
                    testAccount.getId(), 2500, Currency.EUR,
                    "Salaire Février", LocalDate.now(), TransactionType.INCOME
            );

            handler.handle(command);

            // Compte crédité : 1000 + 2500 = 3500
            Account updated = accountRepository.findById(testAccount.getId()).get();
            assertThat(updated.getBalance().money().amount())
                    .isEqualByComparingTo(BigDecimal.valueOf(3500));
        }
    }

    @Nested
    @DisplayName("Validations")
    class Validations {

        @Test
        @DisplayName("doit rejeter si le compte n'existe pas")
        void shouldRejectUnknownAccount() {
            AccountId unknownId = AccountId.of("unknown-account");
            CreateTransactionCommand command = new CreateTransactionCommand(
                    unknownId, 100, Currency.EUR,
                    "Test", LocalDate.now(), TransactionType.EXPENSE
            );

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("doit propager l'erreur si la description est vide")
        void shouldRejectBlankDescription() {
            CreateTransactionCommand command = new CreateTransactionCommand(
                    testAccount.getId(), 100, Currency.EUR,
                    "", LocalDate.now(), TransactionType.EXPENSE
            );

            assertThatThrownBy(() -> handler.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("description");
        }
    }
}
