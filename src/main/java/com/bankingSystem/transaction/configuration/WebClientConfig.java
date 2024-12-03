package com.bankingSystem.transaction.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for creating a {@link WebClient} bean.
 * This class provides a centralized configuration for the {@link WebClient},
 * which is used to perform reactive HTTP requests within the application.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a {@link WebClient} instance as a Spring bean.
     * This {@link WebClient} can be used throughout the application to make
     * non-blocking HTTP requests.*
     */
    @Bean
    public WebClient createWebClient() {
        return WebClient.create();
    }
}

