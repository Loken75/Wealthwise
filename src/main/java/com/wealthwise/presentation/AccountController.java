package com.wealthwise.presentation;

import com.wealthwise.application.command.CreateAccountCommand;
import com.wealthwise.application.command.CreateAccountCommandHandler;
import com.wealthwise.domain.account.model.Account;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.account.port.AccountRepository;
import com.wealthwise.presentation.dto.AccountResponse;
import com.wealthwise.presentation.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur REST pour les comptes.
 *
 * @RestController = @Controller + @ResponseBody
 * Indique à Spring que cette classe gère des requêtes HTTP
 * et que les valeurs retournées sont sérialisées en JSON automatiquement.
 *
 * @RequestMapping("/api/accounts") = toutes les routes de ce contrôleur
 * commencent par /api/accounts.
 *
 * Le contrôleur est MINCE : il ne fait que :
 * 1. Recevoir la requête HTTP et la valider (@Valid)
 * 2. Convertir le DTO en Command
 * 3. Déléguer au Handler (use case)
 * 4. Convertir le résultat en Response DTO
 * 5. Retourner la réponse HTTP avec le bon code de statut
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final CreateAccountCommandHandler createAccountHandler;
    private final AccountRepository accountRepository;

    /**
     * Spring injecte automatiquement les beans déclarés dans ApplicationConfig.
     * C'est l'injection de dépendance par constructeur.
     */
    public AccountController(CreateAccountCommandHandler createAccountHandler,
                             AccountRepository accountRepository) {
        this.createAccountHandler = createAccountHandler;
        this.accountRepository = accountRepository;
    }

    /**
     * POST /api/accounts — Créer un nouveau compte.
     *
     * @Valid déclenche la validation Jakarta sur le DTO (les @NotBlank, @NotNull).
     * Si la validation échoue, Spring retourne automatiquement un 400 Bad Request.
     *
     * ResponseEntity permet de contrôler le code HTTP de retour.
     * 201 Created est la convention REST pour une ressource créée avec succès.
     * Le header Location contient l'URL de la ressource créée.
     */
    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = new CreateAccountCommand(
                request.name(), request.type(), request.currency()
        );

        AccountId id = createAccountHandler.handle(command);

        Account account = accountRepository.findById(id).orElseThrow();
        AccountResponse response = AccountResponse.from(account);

        return ResponseEntity
                .created(URI.create("/api/accounts/" + id.value()))
                .body(response);
    }

    /**
     * GET /api/accounts — Lister tous les comptes.
     */
    @GetMapping
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    /**
     * GET /api/accounts/{id} — Récupérer un compte par son identifiant.
     *
     * @PathVariable extrait la valeur {id} de l'URL.
     * Si le compte n'existe pas, on retourne 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable String id) {
        return accountRepository.findById(AccountId.of(id))
                .map(AccountResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
