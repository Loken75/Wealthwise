package com.wealthwise.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealthwise.presentation.dto.AccountResponse;
import com.wealthwise.presentation.dto.CreateAccountRequest;
import com.wealthwise.domain.account.model.AccountType;
import com.wealthwise.domain.shared.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'API Account.
 *
 * @SpringBootTest démarre le contexte Spring COMPLET (tous les beans,
 * toute la configuration). C'est un vrai test de bout en bout dans le serveur.
 *
 * @AutoConfigureMockMvc fournit un MockMvc qui simule des requêtes HTTP
 * sans démarrer un vrai serveur Tomcat. Plus rapide qu'un vrai serveur
 * mais teste tout le pipeline Spring (sérialisation, validation, routing...).
 *
 * @Autowired est l'injection de dépendance de Spring dans les tests.
 * Spring crée les objets et les injecte automatiquement.
 *
 * ObjectMapper est la bibliothèque Jackson qui convertit Java ↔ JSON.
 * Spring Boot le configure automatiquement.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API Account - Tests d'intégration")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/accounts")
    class CreateAccount {

        @Test
        @DisplayName("doit créer un compte et retourner 201 Created")
        void shouldCreateAccountAndReturn201() throws Exception {
            CreateAccountRequest request = new CreateAccountRequest(
                    "Compte Courant", AccountType.CHECKING, Currency.EUR
            );

            MvcResult result = mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Compte Courant"))
                    .andExpect(jsonPath("$.type").value("CHECKING"))
                    .andExpect(jsonPath("$.currency").value("EUR"))
                    .andExpect(jsonPath("$.balance").value(0.0))
                    .andExpect(jsonPath("$.closed").value(false))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andReturn();

            // Vérifier le header Location
            String location = result.getResponse().getHeader("Location");
            assertThat(location).startsWith("/api/accounts/");
        }

        @Test
        @DisplayName("doit retourner 400 si le nom est vide")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            CreateAccountRequest request = new CreateAccountRequest(
                    "", AccountType.CHECKING, Currency.EUR
            );

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("doit retourner 400 si le type est null")
        void shouldReturn400WhenTypeIsNull() throws Exception {
            String json = """
                    {"name": "Test", "currency": "EUR"}
                    """;

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts")
    class GetAccounts {

        @Test
        @DisplayName("doit retourner la liste des comptes")
        void shouldReturnAccountsList() throws Exception {
            // Créer un compte d'abord
            CreateAccountRequest request = new CreateAccountRequest(
                    "Livret A", AccountType.SAVINGS, Currency.EUR
            );
            mockMvc.perform(post("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Lister
            mockMvc.perform(get("/api/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
        }

        @Test
        @DisplayName("doit retourner 404 pour un compte inexistant")
        void shouldReturn404ForUnknownAccount() throws Exception {
            mockMvc.perform(get("/api/accounts/unknown-id-12345"))
                    .andExpect(status().isNotFound());
        }
    }
}
