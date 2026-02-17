package com.wealthwise.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.domain.transaction.model.TransactionType;
import com.wealthwise.presentation.dto.CreateAccountRequest;
import com.wealthwise.presentation.dto.CreateTransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test d'intégration du flux complet :
 * Créer un compte → Déposer de l'argent → Faire une dépense → Vérifier le solde.
 *
 * Ce test vérifie que toutes les couches collaborent correctement :
 * Controller → Handler → Domain → Repository → réponse JSON.
 *
 * C'est le test le plus proche de l'utilisation réelle de l'API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Flux Transaction - Tests d'intégration")
class TransactionFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accountId;

    /**
     * Crée un compte de test avant chaque test.
     * Extrait l'ID du compte depuis la réponse JSON.
     */
    @BeforeEach
    void setUp() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test Account", AccountType.CHECKING, Currency.EUR
        );

        MvcResult result = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        accountId = response.get("id").asText();
    }

    @Test
    @DisplayName("flux complet : dépôt → dépense → vérification du solde")
    void shouldHandleFullTransactionFlow() throws Exception {
        // 1. Déposer 2500€ (salaire)
        CreateTransactionRequest income = new CreateTransactionRequest(
                accountId, 2500.0, Currency.EUR,
                "Salaire", LocalDate.of(2026, 2, 1), TransactionType.INCOME
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(income)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.amount").value(2500.0));

        // 2. Dépenser 150€ (courses)
        CreateTransactionRequest expense = new CreateTransactionRequest(
                accountId, 150.0, Currency.EUR,
                "Courses", LocalDate.of(2026, 2, 15), TransactionType.EXPENSE
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("EXPENSE"));

        // 3. Vérifier le solde : 2500 - 150 = 2350
        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(2350.0));
    }

    @Test
    @DisplayName("doit rejeter une dépense quand les fonds sont insuffisants")
    void shouldRejectExpenseWhenInsufficientFunds() throws Exception {
        // Le compte a un solde de 0€, on essaie de dépenser 100€
        CreateTransactionRequest expense = new CreateTransactionRequest(
                accountId, 100.0, Currency.EUR,
                "Dépense impossible", LocalDate.now(), TransactionType.EXPENSE
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Insufficient")
                ));
    }

    @Test
    @DisplayName("doit rejeter une transaction sur un compte inexistant")
    void shouldRejectTransactionForUnknownAccount() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "compte-inexistant", 100.0, Currency.EUR,
                "Test", LocalDate.now(), TransactionType.EXPENSE
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("not found")
                ));
    }

    @Test
    @DisplayName("doit rejeter une transaction avec description vide")
    void shouldRejectTransactionWithBlankDescription() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                accountId, 100.0, Currency.EUR,
                "", LocalDate.now(), TransactionType.INCOME
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
