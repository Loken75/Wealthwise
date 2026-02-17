package com.wealthwise.application.command;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.domain.transaction.model.Transaction;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.model.TransactionType;
import com.wealthwise.domain.transaction.port.TransactionRepository;

/**
 * Use Case : créer une transaction et mettre à jour le solde du compte.
 *
 * C'est le handler le plus intéressant car il ORCHESTRE deux agrégats :
 * - Transaction (création)
 * - Account (mise à jour du solde)
 *
 * En DDD, un seul Use Case peut toucher plusieurs agrégats.
 * C'est la couche Application qui coordonne, pas le domaine.
 * Chaque agrégat protège ses propres invariants.
 *
 * Flux :
 * 1. Trouver le compte (ou lever une exception)
 * 2. Créer la transaction (le domaine valide)
 * 3. Mettre à jour le solde du compte (credit ou debit selon le type)
 * 4. Persister les deux agrégats
 */
public class CreateTransactionCommandHandler {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public CreateTransactionCommandHandler(TransactionRepository transactionRepository,
                                           AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionId handle(CreateTransactionCommand command) {
        // 1. Trouver le compte
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + command.accountId().value()
                ));

        // 2. Créer la transaction (le domaine valide le montant, la description, etc.)
        Money money = Money.of(command.amount(), command.currency());
        Transaction transaction = Transaction.create(
                command.accountId(),
                money,
                command.description(),
                command.date(),
                command.type()
        );

        // 3. Mettre à jour le solde du compte selon le type de transaction
        if (command.type() == TransactionType.INCOME) {
            account.credit(money);
        } else if (command.type() == TransactionType.EXPENSE) {
            account.debit(money);
        }
        // TRANSFER sera géré plus tard (il touche 2 comptes)

        // 4. Persister
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return transaction.getId();
    }
}
