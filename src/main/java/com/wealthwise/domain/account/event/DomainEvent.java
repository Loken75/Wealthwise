package com.wealthwise.domain.account.event;

import java.time.LocalDateTime;

/**
 * Interface marqueur pour tous les evenements du domaine.
 *
 * Un Domain Event represente quelque chose qui S'EST PASSE dans le domaine.
 * Il est toujours au passe : AccountCreated (pas CreateAccount).
 * Il est immutable et contient toutes les infos necessaires.
 */
public interface DomainEvent {
    LocalDateTime occurredAt();
}