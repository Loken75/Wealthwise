## 🏗️ Architecture Globale du Projet

### **Nom du projet : MoneyTracker**

### **Vue d'ensemble de l'architecture**

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (Angular)                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Dashboard │  │Transactions│ │Budget   │  │Reports  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │ REST API
┌─────────────────────────────────────────────────────────────┐
│              Backend (Java 21 + Spring Boot 3.2)             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           API Layer (Presentation)                      │ │
│  │  Controllers + DTOs + OpenAPI Documentation            │ │
│  └────────────────────────────────────────────────────────┘ │
│                            │                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         Application Layer (Use Cases)                   │ │
│  │  Commands (CQRS Write) + Queries (CQRS Read)           │ │
│  └────────────────────────────────────────────────────────┘ │
│                            │                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Domain Layer (Business Logic)              │ │
│  │  Aggregates │ Entities │ Value Objects │ Domain Events │ │
│  └────────────────────────────────────────────────────────┘ │
│                            │                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         Infrastructure Layer                            │ │
│  │  Persistence │ External APIs │ Security │ Jobs          │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                    Data & Services Layer                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │PostgreSQL│  │  Redis   │  │RabbitMQ  │  │External  │   │
│  │   (DB)   │  │ (Cache)  │  │ (Events) │  │   APIs   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📐 Domain-Driven Design - Bounded Contexts

### **1. Account Context** (Contexte Compte)
**Responsabilité :** Gestion des comptes bancaires et portefeuilles

**Aggregates :**
- `Account` (Aggregate Root)
  - Value Objects: `AccountNumber`, `Balance`, `Currency`
  - Entities: `AccountHistory`

**Domain Events :**
- `AccountCreated`
- `AccountBalanceUpdated`
- `AccountClosed`

---

### **2. Transaction Context** (Contexte Transaction)
**Responsabilité :** Gestion des transactions financières

**Aggregates :**
- `Transaction` (Aggregate Root)
  - Value Objects: `Amount`, `TransactionDate`, `Description`
  - Entities: `Attachment`

**Domain Events :**
- `TransactionCreated`
- `TransactionCategorized`
- `TransactionUpdated`
- `TransactionDeleted`

---

### **3. Budget Context** (Contexte Budget)
**Responsabilité :** Définition et suivi des budgets

**Aggregates :**
- `Budget` (Aggregate Root)
  - Value Objects: `BudgetPeriod`, `BudgetLimit`
  - Entities: `BudgetAlert`

**Domain Events :**
- `BudgetCreated`
- `BudgetExceeded`
- `BudgetWarningReached`

---

### **4. Category Context** (Contexte Catégorie)
**Responsabilité :** Gestion des catégories de dépenses/revenus

**Aggregates :**
- `Category` (Aggregate Root)
  - Value Objects: `CategoryType`, `Color`, `Icon`

---

### **5. Analytics Context** (Contexte Analytique)
**Responsabilité :** Génération de rapports et statistiques

**Read Models (CQRS) :**
- `MonthlyReport`
- `CategoryReport`
- `TrendAnalysis`

---

## 📁 Structure Complète du Projet

```
moneytracker/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── moneytracker/
│   │   │   │           ├── MoneyTrackerApplication.java
│   │   │   │           │
│   │   │   │           ├── domain/                    # COUCHE DOMAINE
│   │   │   │           │   ├── account/
│   │   │   │           │   │   ├── model/
│   │   │   │           │   │   │   ├── Account.java              # Aggregate Root
│   │   │   │           │   │   │   ├── AccountNumber.java        # Value Object
│   │   │   │           │   │   │   ├── Balance.java              # Value Object
│   │   │   │           │   │   │   ├── Currency.java             # Enum
│   │   │   │           │   │   │   └── AccountType.java          # Enum
│   │   │   │           │   │   ├── port/
│   │   │   │           │   │   │   ├── AccountRepository.java    # Interface
│   │   │   │           │   │   │   └── BalanceCalculator.java    # Interface
│   │   │   │           │   │   ├── service/
│   │   │   │           │   │   │   └── AccountDomainService.java
│   │   │   │           │   │   └── event/
│   │   │   │           │   │       ├── AccountCreated.java
│   │   │   │           │   │       └── AccountBalanceUpdated.java
│   │   │   │           │   │
│   │   │   │           │   ├── transaction/
│   │   │   │           │   │   ├── model/
│   │   │   │           │   │   │   ├── Transaction.java          # Aggregate Root
│   │   │   │           │   │   │   ├── Amount.java               # Value Object
│   │   │   │           │   │   │   ├── TransactionDate.java      # Value Object
│   │   │   │           │   │   │   ├── TransactionType.java      # Enum (INCOME/EXPENSE)
│   │   │   │           │   │   │   └── RecurringPattern.java     # Value Object
│   │   │   │           │   │   ├── port/
│   │   │   │           │   │   │   ├── TransactionRepository.java
│   │   │   │           │   │   │   └── CategorizationService.java # Interface
│   │   │   │           │   │   ├── service/
│   │   │   │           │   │   │   ├── TransactionDomainService.java
│   │   │   │           │   │   │   └── RecurringTransactionService.java
│   │   │   │           │   │   └── event/
│   │   │   │           │   │       ├── TransactionCreated.java
│   │   │   │           │   │       └── TransactionCategorized.java
│   │   │   │           │   │
│   │   │   │           │   ├── budget/
│   │   │   │           │   │   ├── model/
│   │   │   │           │   │   │   ├── Budget.java               # Aggregate Root
│   │   │   │           │   │   │   ├── BudgetPeriod.java         # Value Object
│   │   │   │           │   │   │   ├── BudgetLimit.java          # Value Object
│   │   │   │           │   │   │   └── BudgetAlert.java          # Entity
│   │   │   │           │   │   ├── port/
│   │   │   │           │   │   │   └── BudgetRepository.java
│   │   │   │           │   │   ├── service/
│   │   │   │           │   │   │   └── BudgetTrackingService.java
│   │   │   │           │   │   └── event/
│   │   │   │           │   │       ├── BudgetCreated.java
│   │   │   │           │   │       └── BudgetExceeded.java
│   │   │   │           │   │
│   │   │   │           │   ├── category/
│   │   │   │           │   │   ├── model/
│   │   │   │           │   │   │   ├── Category.java
│   │   │   │           │   │   │   ├── CategoryType.java
│   │   │   │           │   │   │   └── CategoryIcon.java
│   │   │   │           │   │   └── port/
│   │   │   │           │   │       └── CategoryRepository.java
│   │   │   │           │   │
│   │   │   │           │   ├── shared/                    # Shared Kernel
│   │   │   │           │   │   ├── Money.java              # Value Object partagé
│   │   │   │           │   │   ├── DateRange.java
│   │   │   │           │   │   └── UserId.java
│   │   │   │           │   │
│   │   │   │           │   └── exception/
│   │   │   │           │       ├── DomainException.java
│   │   │   │           │       ├── InsufficientBalanceException.java
│   │   │   │           │       └── BudgetExceededException.java
│   │   │   │           │
│   │   │   │           ├── application/              # COUCHE APPLICATION
│   │   │   │           │   ├── command/              # CQRS - Commands (Write)
│   │   │   │           │   │   ├── account/
│   │   │   │           │   │   │   ├── CreateAccountCommand.java
│   │   │   │           │   │   │   ├── CreateAccountCommandHandler.java
│   │   │   │           │   │   │   ├── UpdateAccountCommand.java
│   │   │   │           │   │   │   └── UpdateAccountCommandHandler.java
│   │   │   │           │   │   ├── transaction/
│   │   │   │           │   │   │   ├── CreateTransactionCommand.java
│   │   │   │           │   │   │   ├── CreateTransactionCommandHandler.java
│   │   │   │           │   │   │   ├── CategorizeTransactionCommand.java
│   │   │   │           │   │   │   └── CategorizeTransactionCommandHandler.java
│   │   │   │           │   │   └── budget/
│   │   │   │           │   │       ├── CreateBudgetCommand.java
│   │   │   │           │   │       └── CreateBudgetCommandHandler.java
│   │   │   │           │   │
│   │   │   │           │   ├── query/                # CQRS - Queries (Read)
│   │   │   │           │   │   ├── account/
│   │   │   │           │   │   │   ├── GetAccountQuery.java
│   │   │   │           │   │   │   ├── GetAccountQueryHandler.java
│   │   │   │           │   │   │   └── ListAccountsQuery.java
│   │   │   │           │   │   ├── transaction/
│   │   │   │           │   │   │   ├── GetTransactionQuery.java
│   │   │   │           │   │   │   ├── SearchTransactionsQuery.java
│   │   │   │           │   │   │   └── SearchTransactionsQueryHandler.java
│   │   │   │           │   │   └── analytics/
│   │   │   │           │   │       ├── GetMonthlyReportQuery.java
│   │   │   │           │   │       ├── GetMonthlyReportQueryHandler.java
│   │   │   │           │   │       ├── GetCategoryBreakdownQuery.java
│   │   │   │           │   │       └── GetSpendingTrendQuery.java
│   │   │   │           │   │
│   │   │   │           │   ├── usecase/              # Use Cases complexes
│   │   │   │           │   │   ├── ImportBankStatementUseCase.java
│   │   │   │           │   │   ├── AutoCategorizationUseCase.java
│   │   │   │           │   │   └── GenerateReportUseCase.java
│   │   │   │           │   │
│   │   │   │           │   └── dto/                  # DTOs Application
│   │   │   │           │       ├── AccountDto.java
│   │   │   │           │       ├── TransactionDto.java
│   │   │   │           │       └── BudgetDto.java
│   │   │   │           │
│   │   │   │           ├── infrastructure/          # COUCHE INFRASTRUCTURE
│   │   │   │           │   ├── persistence/
│   │   │   │           │   │   ├── jpa/
│   │   │   │           │   │   │   ├── entity/
│   │   │   │           │   │   │   │   ├── AccountEntity.java
│   │   │   │           │   │   │   │   ├── TransactionEntity.java
│   │   │   │           │   │   │   │   └── BudgetEntity.java
│   │   │   │           │   │   │   ├── repository/
│   │   │   │           │   │   │   │   ├── JpaAccountRepository.java
│   │   │   │           │   │   │   │   ├── JpaTransactionRepository.java
│   │   │   │           │   │   │   │   └── JpaBudgetRepository.java
│   │   │   │           │   │   │   └── mapper/
│   │   │   │           │   │   │       ├── AccountEntityMapper.java
│   │   │   │           │   │   │       └── TransactionEntityMapper.java
│   │   │   │           │   │   └── adapter/
│   │   │   │           │   │       ├── AccountRepositoryAdapter.java
│   │   │   │           │   │       └── TransactionRepositoryAdapter.java
│   │   │   │           │   │
│   │   │   │           │   ├── api/                  # Clients API externes
│   │   │   │           │   │   ├── categorization/
│   │   │   │           │   │   │   ├── CategorizationApiClient.java
│   │   │   │           │   │   │   └── CategorizationServiceAdapter.java
│   │   │   │           │   │   └── exchange/
│   │   │   │           │   │       └── ExchangeRateClient.java
│   │   │   │           │   │
│   │   │   │           │   ├── parser/               # Parseurs de relevés
│   │   │   │           │   │   ├── BankStatementParser.java    # Interface
│   │   │   │           │   │   ├── CsvBankStatementParser.java
│   │   │   │           │   │   └── PdfBankStatementParser.java
│   │   │   │           │   │
│   │   │   │           │   ├── messaging/            # Event Bus
│   │   │   │           │   │   ├── RabbitMqEventPublisher.java
│   │   │   │           │   │   └── listener/
│   │   │   │           │   │       ├── BudgetEventListener.java
│   │   │   │           │   │       └── NotificationEventListener.java
│   │   │   │           │   │
│   │   │   │           │   ├── security/
│   │   │   │           │   │   ├── SecurityConfig.java
│   │   │   │           │   │   ├── JwtTokenProvider.java
│   │   │   │           │   │   └── UserDetailsServiceImpl.java
│   │   │   │           │   │
│   │   │   │           │   ├── cache/
│   │   │   │           │   │   ├── CacheConfig.java
│   │   │   │           │   │   └── RedisCacheAdapter.java
│   │   │   │           │   │
│   │   │   │           │   ├── job/                  # Scheduled Jobs
│   │   │   │           │   │   ├── RecurringTransactionJob.java
│   │   │   │           │   │   ├── BudgetAlertJob.java
│   │   │   │           │   │   └── MonthlyReportJob.java
│   │   │   │           │   │
│   │   │   │           │   └── config/
│   │   │   │           │       ├── DatabaseConfig.java
│   │   │   │           │       ├── RabbitMqConfig.java
│   │   │   │           │       └── RedisConfig.java
│   │   │   │           │
│   │   │   │           └── presentation/            # COUCHE PRÉSENTATION
│   │   │   │               ├── controller/
│   │   │   │               │   ├── AccountController.java
│   │   │   │               │   ├── TransactionController.java
│   │   │   │               │   ├── BudgetController.java
│   │   │   │               │   ├── CategoryController.java
│   │   │   │               │   ├── AnalyticsController.java
│   │   │   │               │   └── ImportController.java
│   │   │   │               │
│   │   │   │               ├── dto/                  # API DTOs
│   │   │   │               │   ├── request/
│   │   │   │               │   │   ├── CreateAccountRequest.java
│   │   │   │               │   │   ├── CreateTransactionRequest.java
│   │   │   │               │   │   └── CreateBudgetRequest.java
│   │   │   │               │   └── response/
│   │   │   │               │       ├── AccountResponse.java
│   │   │   │               │       ├── TransactionResponse.java
│   │   │   │               │       └── MonthlyReportResponse.java
│   │   │   │               │
│   │   │   │               ├── mapper/               # Domain ↔ API DTO
│   │   │   │               │   ├── AccountDtoMapper.java
│   │   │   │               │   └── TransactionDtoMapper.java
│   │   │   │               │
│   │   │   │               └── exception/
│   │   │   │                   ├── GlobalExceptionHandler.java
│   │   │   │                   └── ErrorResponse.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/
│   │   │           └── migration/
│   │   │               ├── V1__create_account_table.sql
│   │   │               ├── V2__create_transaction_table.sql
│   │   │               └── V3__create_budget_table.sql
│   │   │
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── moneytracker/
│   │       │           ├── unit/                     # Tests Unitaires (TDD)
│   │       │           │   ├── domain/
│   │       │           │   │   ├── account/
│   │       │           │   │   │   ├── AccountTest.java
│   │       │           │   │   │   └── BalanceTest.java
│   │       │           │   │   ├── transaction/
│   │       │           │   │   │   ├── TransactionTest.java
│   │       │           │   │   │   └── AmountTest.java
│   │       │           │   │   └── budget/
│   │       │           │   │       └── BudgetTest.java
│   │       │           │   │
│   │       │           │   └── application/
│   │       │           │       └── command/
│   │       │           │           └── CreateTransactionCommandHandlerTest.java
│   │       │           │
│   │       │           ├── integration/              # Tests d'Intégration
│   │       │           │   ├── repository/
│   │       │           │   │   ├── AccountRepositoryTest.java
│   │       │           │   │   └── TransactionRepositoryTest.java
│   │       │           │   ├── api/
│   │       │           │   │   └── TransactionControllerTest.java
│   │       │           │   └── AbstractIntegrationTest.java  # Base avec Testcontainers
│   │       │           │
│   │       │           ├── acceptance/               # Tests BDD (Cucumber)
│   │       │           │   ├── CucumberSpringConfiguration.java
│   │       │           │   ├── steps/
│   │       │           │   │   ├── AccountSteps.java
│   │       │           │   │   ├── TransactionSteps.java
│   │       │           │   │   └── BudgetSteps.java
│   │       │           │   └── support/
│   │       │           │       └── TestContext.java
│   │       │           │
│   │       │           └── architecture/             # Tests ArchUnit
│   │       │               ├── LayeredArchitectureTest.java
│   │       │               └── NamingConventionTest.java
│   │       │
│   │       └── resources/
│   │           ├── application-test.yml
│   │           └── features/                         # Fichiers Gherkin
│   │               ├── account.feature
│   │               ├── transaction.feature
│   │               ├── budget.feature
│   │               └── categorization.feature
│   │
│   ├── pom.xml
│   ├── Dockerfile
│   └── .dockerignore
│
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   │   ├── services/
│   │   │   │   ├── guards/
│   │   │   │   └── interceptors/
│   │   │   ├── shared/
│   │   │   │   ├── components/
│   │   │   │   ├── directives/
│   │   │   │   └── pipes/
│   │   │   ├── features/
│   │   │   │   ├── dashboard/
│   │   │   │   ├── accounts/
│   │   │   │   ├── transactions/
│   │   │   │   ├── budgets/
│   │   │   │   └── analytics/
│   │   │   ├── app.component.ts
│   │   │   └── app.routes.ts
│   │   ├── assets/
│   │   └── environments/
│   ├── package.json
│   ├── angular.json
│   ├── Dockerfile
│   └── nginx.conf
│
├── docker/
│   ├── docker-compose.yml
│   ├── docker-compose.prod.yml
│   └── init-scripts/
│       └── init-db.sql
│
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── backend/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── hpa.yaml
│   ├── frontend/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   ├── postgres/
│   │   ├── statefulset.yaml
│   │   ├── service.yaml
│   │   └── pvc.yaml
│   ├── redis/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── ingress.yaml
│
└── README.md
```

---

## 🎯 User Stories et Scénarios BDD

### **Fichier : `transaction.feature`**

```gherkin
Feature: Gestion des transactions financières
  En tant qu'utilisateur
  Je veux gérer mes transactions
  Afin de suivre mes dépenses et revenus

  Background:
    Given je suis connecté en tant qu'utilisateur "john@example.com"
    And j'ai un compte bancaire "Compte Courant" avec un solde de 1000.00€

  Scenario: Créer une nouvelle dépense
    When je crée une transaction avec les détails suivants:
      | montant     | -50.00€         |
      | description | Courses Carrefour |
      | date        | 2024-02-15      |
      | compte      | Compte Courant  |
    Then la transaction doit être créée avec succès
    And le solde du compte "Compte Courant" doit être de 950.00€
    And je dois recevoir une notification "Transaction créée"

  Scenario: Catégorisation automatique basée sur l'historique
    Given j'ai les transactions historiques suivantes:
      | description       | catégorie     |
      | Carrefour Market  | Alimentation  |
      | Leclerc Drive     | Alimentation  |
      | Auchan Express    | Alimentation  |
    When je crée une transaction "Courses Carrefour" de -45.00€
    Then la transaction doit être automatiquement catégorisée en "Alimentation"
    And le niveau de confiance doit être supérieur à 80%

  Scenario: Refus de transaction si solde insuffisant (avec règle métier)
    Given j'ai activé la règle "Empêcher découvert"
    When j'essaie de créer une transaction de -1200.00€
    Then la transaction doit être rejetée
    And je dois recevoir l'erreur "Solde insuffisant"
    And le solde du compte doit rester à 1000.00€

  Scenario: Import de relevé bancaire CSV
    When j'importe le fichier de relevé "releve_janvier.csv" contenant:
      | date       | description    | montant |
      | 2024-01-01 | Salaire        | 2500.00 |
      | 2024-01-05 | Loyer          | -800.00 |
      | 2024-01-10 | EDF            | -75.00  |
    Then 3 transactions doivent être créées
    And le solde doit être de 2625.00€
    And un rapport d'import doit être généré
```

### **Fichier : `budget.feature`**

```gherkin
Feature: Gestion des budgets
  En tant qu'utilisateur
  Je veux définir des budgets par catégorie
  Afin de contrôler mes dépenses

  Background:
    Given je suis connecté en tant qu'utilisateur "john@example.com"
    And j'ai les catégories suivantes:
      | nom          | type    |
      | Alimentation | EXPENSE |
      | Restaurants  | EXPENSE |
      | Transports   | EXPENSE |

  Scenario: Créer un budget mensuel
    When je crée un budget avec les détails suivants:
      | catégorie   | Alimentation |
      | limite      | 400.00€      |
      | période     | Mensuel      |
      | mois        | Février 2024 |
    Then le budget doit être créé avec succès
    And le montant dépensé initial doit être 0.00€
    And le pourcentage d'utilisation doit être 0%

  Scenario: Alerte de dépassement à 80% du budget
    Given j'ai un budget "Alimentation" de 500.00€ pour février 2024
    And j'ai déjà dépensé 350.00€ en alimentation
    When je crée une transaction "Carrefour" de -50.00€ en "Alimentation"
    Then je dois recevoir une alerte "Budget à 80%"
    And l'alerte doit contenir "Il reste 100.00€ sur 500.00€"
    And le statut du budget doit être "WARNING"

  Scenario: Dépassement de budget
    Given j'ai un budget "Restaurants" de 200.00€ pour février 2024
    And j'ai déjà dépensé 180.00€ en restaurants
    When je crée une transaction "Restaurant Italien" de -30.00€ en "Restaurants"
    Then la transaction doit être créée malgré le dépassement
    And je dois recevoir une alerte "Budget dépassé de 10.00€"
    And le statut du budget doit être "EXCEEDED"
    And une notification push doit être envoyée

  Scenario: Calcul du budget restant en temps réel
    Given j'ai un budget "Transports" de 150.00€ pour février 2024
    And j'ai dépensé:
      | date       | description | montant |
      | 2024-02-01 | Métro       | -20.00  |
      | 2024-02-10 | Essence     | -45.00  |
    When je consulte mon budget "Transports"
    Then le montant dépensé doit être 65.00€
    And le montant restant doit être 85.00€
    And le pourcentage d'utilisation doit être 43%
    And le statut doit être "ON_TRACK"
```

### **Fichier : `categorization.feature`**

```gherkin
Feature: Catégorisation automatique des transactions
  En tant que système
  Je veux catégoriser automatiquement les transactions
  Afin de réduire la saisie manuelle

  Scenario: Catégorisation par mots-clés exacts
    Given le système a les règles de catégorisation suivantes:
      | mot-clé  | catégorie    |
      | SNCF     | Transports   |
      | RATP     | Transports   |
      | Carrefour| Alimentation |
    When une transaction "PAIEMENT CB SNCF PARIS" est créée
    Then elle doit être catégorisée en "Transports"
    And le niveau de confiance doit être "HIGH"

  Scenario: Apprentissage basé sur corrections utilisateur
    Given le système a suggéré "Loisirs" pour "Netflix"
    And l'utilisateur a corrigé en "Abonnements"
    When une nouvelle transaction "Netflix" est créée
    Then elle doit être catégorisée en "Abonnements"
    And la règle doit être mémorisée pour les prochaines fois

  Scenario: Catégorisation ambiguë nécessitant confirmation
    Given le système hésite entre "Santé" et "Beauté" pour "Pharmacie"
    When une transaction "PHARMACIE CENTRALE" est créée
    Then la catégorie suggérée doit être "Santé"
    And le niveau de confiance doit être "MEDIUM"
    And l'utilisateur doit être invité à confirmer
```

---

## 🧪 Plan de Développement TDD (Test-Driven Development)

### **Phase 1 : Domain Layer (Semaine 1-2)**

#### **Jour 1-2 : Value Objects**

**Cycle TDD pour `Money`:**

```java
// 1. RED - Écrire le test qui échoue
@Test
void shouldCreateMoneyWithValidAmount() {
    Money money = Money.of(100.50, Currency.EUR);
    
    assertThat(money.getAmount()).isEqualTo(100.50);
    assertThat(money.getCurrency()).isEqualTo(Currency.EUR);
}

// 2. GREEN - Implémenter le minimum pour passer le test
public class Money {
    private final BigDecimal amount;
    private final Currency currency;
    
    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
}

// 3. REFACTOR - Améliorer le code
// Ajouter validation, immutabilité, méthodes métier

@Test
void shouldRejectNegativeAmount() {
    assertThatThrownBy(() -> Money.of(-10, Currency.EUR))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Amount cannot be negative");
}

@Test
void shouldAddMoneyWithSameCurrency() {
    Money money1 = Money.of(100, Currency.EUR);
    Money money2 = Money.of(50, Currency.EUR);
    
    Money result = money1.add(money2);
    
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(150));
}

@Test
void shouldNotAddMoneyWithDifferentCurrencies() {
    Money euros = Money.of(100, Currency.EUR);
    Money dollars = Money.of(100, Currency.USD);
    
    assertThatThrownBy(() -> euros.add(dollars))
        .isInstanceOf(CurrencyMismatchException.class);
}
```

**Cycle TDD pour `Transaction` (Aggregate):**

```java
// Test 1 : Création basique
@Test
void shouldCreateTransactionWithRequiredFields() {
    Transaction transaction = Transaction.builder()
        .id(TransactionId.generate())
        .accountId(AccountId.of("ACC-001"))
        .amount(Money.of(-50, Currency.EUR))
        .description("Courses Carrefour")
        .date(LocalDate.of(2024, 2, 15))
        .type(TransactionType.EXPENSE)
        .build();
    
    assertThat(transaction).isNotNull();
    assertThat(transaction.isExpense()).isTrue();
}

// Test 2 : Invariants métier
@Test
void shouldRejectExpenseWithPositiveAmount() {
    assertThatThrownBy(() -> 
        Transaction.builder()
            .amount(Money.of(50, Currency.EUR))  // Positif !
            .type(TransactionType.EXPENSE)
            .build()
    ).isInstanceOf(InvalidTransactionException.class)
     .hasMessage("Expense amount must be negative");
}

// Test 3 : Comportements métier
@Test
void shouldCategorizeTransaction() {
    Transaction transaction = createTransaction();
    CategoryId categoryId = CategoryId.of("CAT-001");
    
    transaction.categorize(categoryId, ConfidenceLevel.HIGH);
    
    assertThat(transaction.getCategoryId()).isEqualTo(categoryId);
    assertThat(transaction.getConfidenceLevel()).isEqualTo(ConfidenceLevel.HIGH);
    assertThat(transaction.getDomainEvents())
        .hasSize(1)
        .first()
        .isInstanceOf(TransactionCategorized.class);
}
```

#### **Jour 3-4 : Aggregates et Entities**

**Cycle TDD pour `Budget`:**

```java
@Test
void shouldTrackSpendingAgainstBudget() {
    Budget budget = Budget.create(
        BudgetId.generate(),
        CategoryId.of("CAT-001"),
        Money.of(500, Currency.EUR),
        BudgetPeriod.monthly(YearMonth.of(2024, 2))
    );
    
    budget.recordExpense(Money.of(100, Currency.EUR));
    
    assertThat(budget.getSpentAmount()).isEqualTo(Money.of(100, Currency.EUR));
    assertThat(budget.getRemainingAmount()).isEqualTo(Money.of(400, Currency.EUR));
    assertThat(budget.getUsagePercentage()).isEqualTo(20.0);
    assertThat(budget.getStatus()).isEqualTo(BudgetStatus.ON_TRACK);
}

@Test
void shouldTriggerWarningAt80Percent() {
    Budget budget = createBudgetWithLimit(500);
    
    budget.recordExpense(Money.of(400, Currency.EUR));  // 80%
    
    List<DomainEvent> events = budget.getDomainEvents();
    assertThat(events)
        .filteredOn(e -> e instanceof BudgetWarningReached)
        .hasSize(1);
    assertThat(budget.getStatus()).isEqualTo(BudgetStatus.WARNING);
}

@Test
void shouldMarkAsExceededWhenOverLimit() {
    Budget budget = createBudgetWithLimit(500);
    
    budget.recordExpense(Money.of(600, Currency.EUR));
    
    assertThat(budget.getStatus()).isEqualTo(BudgetStatus.EXCEEDED);
    List<DomainEvent> events = budget.getDomainEvents();
    assertThat(events)
        .filteredOn(e -> e instanceof BudgetExceeded)
        .hasSize(1);
}
```

---

### **Phase 2 : Application Layer (Semaine 3)**

**Cycle TDD pour Command Handler:**

```java
@ExtendWith(MockitoExtension.class)
class CreateTransactionCommandHandlerTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private CreateTransactionCommandHandler handler;
    
    @Test
    void shouldCreateTransactionSuccessfully() {
        // Given
        Account account = createAccount();
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        CreateTransactionCommand command = CreateTransactionCommand.builder()
            .accountId("ACC-001")
            .amount(-50.00)
            .currency("EUR")
            .description("Courses")
            .date(LocalDate.now())
            .build();
        
        // When
        TransactionDto result = handler.handle(command);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(-50.00);
        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher).publish(any(TransactionCreated.class));
    }
    
    @Test
    void shouldRejectTransactionWhenAccountNotFound() {
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        
        CreateTransactionCommand command = createCommand();
        
        assertThatThrownBy(() -> handler.handle(command))
            .isInstanceOf(AccountNotFoundException.class);
        
        verify(transactionRepository, never()).save(any());
    }
}
```

---

### **Phase 3 : Infrastructure Layer (Semaine 4)**

**Tests d'intégration avec Testcontainers:**

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("moneytracker_test")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private JpaTransactionRepository repository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void shouldPersistAndRetrieveTransaction() {
        // Given
        TransactionEntity entity = createTransactionEntity();
        
        // When
        TransactionEntity saved = repository.save(entity);
        Optional<TransactionEntity> retrieved = repository.findById(saved.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getDescription()).isEqualTo("Test Transaction");
        assertThat(retrieved.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(-50.00));
    }
    
    @Test
    void shouldFindTransactionsByDateRange() {
        // Given
        createTransactions(LocalDate.of(2024, 1, 1), 10);
        createTransactions(LocalDate.of(2024, 2, 1), 5);
        
        // When
        List<TransactionEntity> result = repository.findByDateBetween(
            LocalDate.of(2024, 2, 1),
            LocalDate.of(2024, 2, 28)
        );
        
        // Then
        assertThat(result).hasSize(5);
    }
}
```

---

### **Phase 4 : Tests BDD avec Cucumber (Semaine 5)**

**Step Definitions:**

```java
@SpringBootTest
public class TransactionSteps {
    
    @Autowired
    private TestContext testContext;
    
    @Autowired
    private TransactionController transactionController;
    
    @Given("je suis connecté en tant qu'utilisateur {string}")
    public void jesuisConnecte(String email) {
        testContext.setCurrentUser(email);
        // Setup authentication context
    }
    
    @Given("j'ai un compte bancaire {string} avec un solde de {double}€")
    public void jAiUnCompteBancaire(String accountName, Double balance) {
        Account account = Account.create(accountName, Money.of(balance, Currency.EUR));
        testContext.setAccount(account);
    }
    
    @When("je crée une transaction avec les détails suivants:")
    public void jeCreeUneTransaction(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        
        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .amount(parseAmount(data.get("montant")))
            .description(data.get("description"))
            .date(LocalDate.parse(data.get("date")))
            .accountId(testContext.getAccount().getId())
            .build();
        
        ResponseEntity<TransactionResponse> response = transactionController.create(request);
        testContext.setLastResponse(response);
    }
    
    @Then("la transaction doit être créée avec succès")
    public void laTransactionDoitEtreCreee() {
        ResponseEntity<?> response = testContext.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    
    @Then("le solde du compte {string} doit être de {double}€")
    public void leSoldeDuCompteDoit(String accountName, Double expectedBalance) {
        Account account = testContext.getAccount();
        assertThat(account.getBalance().getAmount())
            .isEqualByComparingTo(BigDecimal.valueOf(expectedBalance));
    }
}
```

---

## 🐳 Configuration Docker & Kubernetes

### **Dockerfile Backend (Multi-stage)**

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
```

### **docker-compose.yml (Développement)**

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: moneytracker-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/moneytracker
      - SPRING_DATASOURCE_USERNAME=moneytracker
      - SPRING_DATASOURCE_PASSWORD=secret
      - SPRING_REDIS_HOST=redis
      - SPRING_RABBITMQ_HOST=rabbitmq
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
      rabbitmq:
        condition: service_healthy
    networks:
      - moneytracker-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: moneytracker-frontend
    ports:
      - "4200:80"
    depends_on:
      - backend
    networks:
      - moneytracker-network

  postgres:
    image: postgres:16-alpine
    container_name: moneytracker-db
    environment:
      POSTGRES_DB: moneytracker
      POSTGRES_USER: moneytracker
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./docker/init-scripts:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U moneytracker"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - moneytracker-network

  redis:
    image: redis:7-alpine
    container_name: moneytracker-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - moneytracker-network

  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: moneytracker-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"  # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: moneytracker
      RABBITMQ_DEFAULT_PASS: secret
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - moneytracker-network

volumes:
  postgres-data:
  redis-data:
  rabbitmq-data:

networks:
  moneytracker-network:
    driver: bridge
```

### **Kubernetes Deployment (backend/deployment.yaml)**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: moneytracker-backend
  namespace: moneytracker
  labels:
    app: moneytracker
    component: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: moneytracker
      component: backend
  template:
    metadata:
      labels:
        app: moneytracker
        component: backend
    spec:
      containers:
      - name: backend
        image: moneytracker/backend:latest
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: moneytracker-config
              key: database.url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: moneytracker-secrets
              key: database.username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: moneytracker-secrets
              key: database.password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: moneytracker-backend
  namespace: moneytracker
spec:
  selector:
    app: moneytracker
    component: backend
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: moneytracker-backend-hpa
  namespace: moneytracker
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: moneytracker-backend
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

---

## 📊 Stack Technique Détaillée

### **Backend**

```xml
<!-- pom.xml - Dépendances principales -->
<dependencies>
    <!-- Spring Boot 3.2+ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    
    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    
    <!-- MapStruct pour mappings -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- Apache PDFBox pour parsing PDF -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.1</version>
    </dependency>
    
    <!-- Tests -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.15.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <version>7.15.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.tngtech.archunit</groupId>
        <artifactId>archunit-junit5</artifactId>
        <version>1.2.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
