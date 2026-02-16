package com.wealthwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application WealthWise.
 *
 * L'annotation @SpringBootApplication combine trois choses :
 * - @Configuration : cette classe peut déclarer des beans Spring
 * - @EnableAutoConfiguration : Spring Boot configure automatiquement
 *   les composants en fonction des dépendances présentes
 * - @ComponentScan : Spring scanne ce package et ses sous-packages
 *   pour trouver les composants (@Service, @Controller, etc.)
 */
@SpringBootApplication
public class WealthWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WealthWiseApplication.class, args);
    }
}
