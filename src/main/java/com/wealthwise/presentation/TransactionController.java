package com.wealthwise.presentation;

import com.wealthwise.application.command.*;
import com.wealthwise.domain.account.model.AccountId;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.domain.transaction.model.TransactionId;
import com.wealthwise.domain.transaction.port.TransactionRepository;
import com.wealthwise.presentation.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionCommandHandler createTransactionHandler;
    private final CategorizeTransactionCommandHandler categorizeHandler;
    private final TransactionRepository transactionRepository;

    public TransactionController(CreateTransactionCommandHandler createTransactionHandler,
                                 CategorizeTransactionCommandHandler categorizeHandler,
                                 TransactionRepository transactionRepository) {
        this.createTransactionHandler = createTransactionHandler;
        this.categorizeHandler = categorizeHandler;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        CreateTransactionCommand command = new CreateTransactionCommand(
                AccountId.of(request.accountId()),
                request.amount(),
                request.currency(),
                request.description(),
                request.date(),
                request.type()
        );

        TransactionId id = createTransactionHandler.handle(command);

        var transaction = transactionRepository.findById(id).orElseThrow();
        return ResponseEntity
                .created(URI.create("/api/transactions/" + id.value()))
                .body(TransactionResponse.from(transaction));
    }

    /**
     * PUT /api/transactions/{id}/categorize — Catégoriser une transaction.
     *
     * PUT (et non PATCH) car on remplace entièrement la catégorisation.
     */
    @PutMapping("/{id}/categorize")
    public ResponseEntity<TransactionResponse> categorize(
            @PathVariable String id,
            @Valid @RequestBody CategorizeTransactionRequest request) {

        CategorizeTransactionCommand command = new CategorizeTransactionCommand(
                TransactionId.of(id),
                CategoryId.of(request.categoryId()),
                request.confidenceLevel()
        );

        categorizeHandler.handle(command);

        var transaction = transactionRepository.findById(TransactionId.of(id)).orElseThrow();
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable String id) {
        return transactionRepository.findById(TransactionId.of(id))
                .map(TransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/transactions?accountId=xxx — Lister les transactions d'un compte.
     *
     * @RequestParam extrait les paramètres de l'URL (?accountId=xxx).
     * "required = false" signifie que le paramètre est optionnel.
     */
    @GetMapping
    public List<TransactionResponse> findByAccount(
            @RequestParam(required = false) String accountId) {

        if (accountId != null) {
            return transactionRepository.findByAccountId(AccountId.of(accountId)).stream()
                    .map(TransactionResponse::from)
                    .toList();
        }
        // Si pas de filtre, retourner une liste vide (on n'a pas de findAll)
        return List.of();
    }
}
