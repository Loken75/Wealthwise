package com.wealthwise.infrastructure.persistence.jpa;

import com.wealthwise.infrastructure.persistence.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour les comptes.
 *
 * Spring Data JPA est MAGIQUE : tu définis une interface qui étend JpaRepository,
 * et Spring génère automatiquement l'implémentation avec tous les SQL nécessaires.
 *
 * JpaRepository<AccountEntity, String> signifie :
 * - AccountEntity = le type de l'entité gérée
 * - String = le type de la clé primaire (@Id)
 *
 * Tu obtiens gratuitement : save(), findById(), findAll(), deleteById(), count()...
 *
 * ATTENTION : ce repository travaille avec des AccountEntity (JPA),
 * pas des Account (domaine). L'adaptateur JpaAccountRepositoryAdapter
 * fait la conversion entre les deux.
 */
public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, String> {
    // Tout est hérité de JpaRepository — rien à écrire !
}
