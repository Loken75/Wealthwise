package com.wealthwise.domain.account.model;

import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.shared.Money;

/**
 * Solde d'un compte bancaire.
 *
 * Balance encapsule un Money et ajoute des règles métier financières :
 * - On crédite (dépose) avec un montant positif
 * - On débite (retire) avec un montant positif
 * - On peut vérifier si un débit est possible avant de le faire
 *
 * C'est un record, donc immutable : credit() et debit() retournent
 * un NOUVEAU Balance, l'ancien ne change pas.
 */
public record Balance(Money money) {

    public static Balance of(double amount, Currency currency) {
        return new Balance(Money.of(amount, currency));
    }

    public static Balance zero(Currency currency) {
        return new Balance(Money.zero(currency));
    }

    /**
     * Créditer le compte (déposer de l'argent).
     * Le montant doit être positif — on ne "dépose" pas un montant négatif.
     */
    public Balance credit(Money amount) {
        requirePositive(amount);
        return new Balance(this.money.add(amount));
    }

    /**
     * Débiter le compte (retirer de l'argent).
     * Le montant doit être positif — on ne "retire" pas un montant négatif.
     * Note : on PERMET le découvert ici. C'est à l'Account de décider
     * s'il autorise le découvert ou non (règle métier de niveau supérieur).
     */
    public Balance debit(Money amount) {
        requirePositive(amount);
        return new Balance(this.money.subtract(amount));
    }

    /**
     * Vérifie si le solde permet de débiter le montant donné sans passer en négatif.
     */
    public boolean canDebit(Money amount) {
        return !this.money.subtract(amount).isNegative();
    }

    private void requirePositive(Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException(
                    "Amount must be positive, got: " + amount.amount()
            );
        }
    }
}
