package com.wealthwise.presentation;

import com.wealthwise.application.command.CreateBudgetCommand;
import com.wealthwise.application.command.CreateBudgetCommandHandler;
import com.wealthwise.domain.budget.model.BudgetId;
import com.wealthwise.domain.budget.port.BudgetRepository;
import com.wealthwise.domain.category.model.CategoryId;
import com.wealthwise.presentation.dto.BudgetResponse;
import com.wealthwise.presentation.dto.CreateBudgetRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final CreateBudgetCommandHandler createBudgetHandler;
    private final BudgetRepository budgetRepository;

    public BudgetController(CreateBudgetCommandHandler createBudgetHandler,
                            BudgetRepository budgetRepository) {
        this.createBudgetHandler = createBudgetHandler;
        this.budgetRepository = budgetRepository;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody CreateBudgetRequest request) {
        CreateBudgetCommand command = new CreateBudgetCommand(
                CategoryId.of(request.categoryId()),
                request.limitAmount(),
                request.currency(),
                request.month()
        );

        BudgetId id = createBudgetHandler.handle(command);

        var budget = budgetRepository.findById(id).orElseThrow();
        return ResponseEntity
                .created(URI.create("/api/budgets/" + id.value()))
                .body(BudgetResponse.from(budget));
    }

    @GetMapping
    public List<BudgetResponse> findAll() {
        return budgetRepository.findAll().stream()
                .map(BudgetResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> findById(@PathVariable String id) {
        return budgetRepository.findById(BudgetId.of(id))
                .map(BudgetResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
