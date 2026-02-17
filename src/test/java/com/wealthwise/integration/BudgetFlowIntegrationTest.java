package com.wealthwise.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealthwise.domain.category.model.CategoryType;
import com.wealthwise.domain.shared.Currency;
import com.wealthwise.presentation.dto.CreateBudgetRequest;
import com.wealthwise.presentation.dto.CreateCategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Flux Category + Budget - Tests d'intégration")
class BudgetFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String categoryId;

    @BeforeEach
    void setUp() throws Exception {
        // Créer une catégorie de test
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Transport", CategoryType.EXPENSE, "#3498DB", "bus"
        );

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        categoryId = response.get("id").asText();
    }

    @Test
    @DisplayName("doit créer une catégorie puis un budget associé")
    void shouldCreateCategoryThenBudget() throws Exception {
        CreateBudgetRequest budgetRequest = new CreateBudgetRequest(
                categoryId, 150.0, Currency.EUR, YearMonth.of(2026, 3)
        );

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budgetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(categoryId))
                .andExpect(jsonPath("$.limit").value(150.0))
                .andExpect(jsonPath("$.spent").value(0.0))
                .andExpect(jsonPath("$.status").value("ON_TRACK"));
    }

    @Test
    @DisplayName("doit rejeter un budget pour une catégorie inexistante")
    void shouldRejectBudgetForUnknownCategory() throws Exception {
        CreateBudgetRequest request = new CreateBudgetRequest(
                "categorie-inexistante", 200.0, Currency.EUR, YearMonth.of(2026, 3)
        );

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("not found")
                ));
    }

    @Test
    @DisplayName("doit rejeter une catégorie avec une couleur invalide")
    void shouldRejectCategoryWithInvalidColor() throws Exception {
        String json = """
                {"name": "Test", "type": "EXPENSE", "color": "rouge"}
                """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("doit lister les catégories créées")
    void shouldListCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(
                        org.hamcrest.Matchers.greaterThanOrEqualTo(1)
                ));
    }
}
