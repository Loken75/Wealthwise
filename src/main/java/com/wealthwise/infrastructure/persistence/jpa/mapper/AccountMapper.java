package com.wealthwise.infrastructure.persistence.jpa.mapper;

import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;
import com.wealthwise.infrastructure.persistence.jpa.entity.AccountEntity;

/**
 * Convertit entre Account (domaine) et AccountEntity (JPA).
 *
 * Le mapper est le PONT entre les deux mondes :
 * - toEntity() : domaine → base de données (pour save)
 * - toDomain() : base de données → domaine (pour findById)
 *
 * C'est une classe utilitaire avec des méthodes statiques.
 * Pas besoin d'en faire un bean Spring.
 */
public final class AccountMapper {

    private AccountMapper() {
        // Classe utilitaire, pas d'instanciation
    }

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getId().value(),
                account.getName(),
                account.getType().name(),
                account.getCurrency().name(),
                account.getBalance().money().amount(),
                account.isClosed(),
                account.getCreatedAt()
        );
    }

    /**
     * Reconstruit un objet domaine depuis l'entité JPA.
     *
     * On utilise Account.reconstitute() — une factory method qui
     * recrée un objet domaine SANS déclencher de validation ni d'événements.
     * C'est nécessaire car les données en base sont déjà validées.
     *
     * NOTE : cette méthode n'existe pas encore dans Account.
     * On va l'ajouter juste après.
     */
    public static Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                AccountId.of(entity.getId()),
                entity.getName(),
                AccountType.valueOf(entity.getType()),
                Currency.valueOf(entity.getCurrency()),
                Money.of(entity.getBalance().doubleValue(), Currency.valueOf(entity.getCurrency())),
                entity.isClosed(),
                entity.getCreatedAt()
        );
    }
}
