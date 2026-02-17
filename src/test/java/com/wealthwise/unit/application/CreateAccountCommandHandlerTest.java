package com.wealthwise.unit.application;

import com.wealthwise.application.command.CreateAccountCommand;
import com.wealthwise.application.command.CreateAccountCommandHandler;
import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.unit.infrastructure.fake.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests du Use Case CreateAccount.
 *
 * On utilise le InMemoryAccountRepository (fake) au lieu de mocker.
 * Le @BeforeEach recrée un handler propre avant chaque test
 * pour que les tests soient isolés les uns des autres.
 */
@DisplayName("Use Case : CreateAccount")
class CreateAccountCommandHandlerTest {

    private InMemoryAccountRepository accountRepository;
    private CreateAccountCommandHandler handler;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        handler = new CreateAccountCommandHandler(accountRepository);
    }

    @Test
    @DisplayName("doit créer un compte et le persister")
    void shouldCreateAndPersistAccount() {
        CreateAccountCommand command = new CreateAccountCommand(
                "Compte Courant", AccountType.CHECKING, Currency.EUR
        );

        AccountId id = handler.handle(command);

        // Vérifier que le compte est dans le repository
        assertThat(id).isNotNull();
        assertThat(accountRepository.count()).isEqualTo(1);

        Optional<Account> saved = accountRepository.findById(id);
        assertThat(saved).isPresent();
        assertThat(saved.get().getName()).isEqualTo("Compte Courant");
        assertThat(saved.get().getType()).isEqualTo(AccountType.CHECKING);
        assertThat(saved.get().getCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.get().getBalance().money().isZero()).isTrue();
    }

    @Test
    @DisplayName("doit propager l'erreur du domaine si le nom est vide")
    void shouldPropagatesDomainErrorForBlankName() {
        CreateAccountCommand command = new CreateAccountCommand(
                "", AccountType.CHECKING, Currency.EUR
        );

        // L'erreur vient du domaine (Account.create()), pas du handler
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalArgumentException.class);

        // Rien n'a été persisté
        assertThat(accountRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("doit créer plusieurs comptes indépendants")
    void shouldCreateMultipleAccounts() {
        handler.handle(new CreateAccountCommand("Courant", AccountType.CHECKING, Currency.EUR));
        handler.handle(new CreateAccountCommand("Livret A", AccountType.SAVINGS, Currency.EUR));
        handler.handle(new CreateAccountCommand("Portefeuille", AccountType.CASH, Currency.EUR));

        assertThat(accountRepository.count()).isEqualTo(3);
    }
}
