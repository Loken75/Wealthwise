package com.wealthwise.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'API REST.
 *
 * @RestControllerAdvice intercepte toutes les exceptions lancées
 * par les contrôleurs et les transforme en réponses JSON propres.
 *
 * Sans ceci, Spring retournerait des pages d'erreur HTML par défaut
 * ou des traces de stack Java — pas très utile pour une API.
 *
 * Chaque méthode @ExceptionHandler gère un type d'exception spécifique
 * et retourne le code HTTP approprié :
 * - 400 Bad Request : données invalides
 * - 404 Not Found : ressource inexistante (via IllegalArgument avec "not found")
 * - 409 Conflict : conflit d'état (ex: budget déjà existant)
 * - 500 Internal Server Error : erreur inattendue
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erreurs de validation Jakarta (@NotBlank, @NotNull, @Positive...).
     * Retourne la liste des champs en erreur avec leurs messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid"
                ))
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation failed",
                "details", errors,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Erreurs métier de type "données invalides" ou "ressource non trouvée".
     * IllegalArgumentException est lancée par le domaine pour les validations.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage() != null ? ex.getMessage() : "Invalid request",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Erreurs métier de type "état invalide" (ex: fonds insuffisants, compte fermé).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage() != null ? ex.getMessage() : "Invalid state",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Filet de sécurité pour les erreurs imprévues.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", "An unexpected error occurred",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
