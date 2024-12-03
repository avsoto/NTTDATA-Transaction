package com.bankingSystem.transaction.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for account service integration.
 * This class is responsible for setting up and providing the base URL of the account service
 * used within the application.
 */
@Configuration
public class AccountServiceConfig {

    /**
     * The URL of the account microservice.
     * This value is injected from the application properties using the key {@code microservice.accounts.url}.
     */
    @Value("${microservice.accounts.url}")
    private String accountServiceUrl;

    /**
     * Provides the base URL of the account service as a Spring bean.
     */
    @Bean
    public String accountServiceBaseUrl() {
        return accountServiceUrl;
    }
}

