package com.bankingSystem.transaction.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountServiceConfig {

    @Value("${microservice.accounts.url}")
    private String accountServiceUrl;

    @Bean
    public String accountServiceBaseUrl() {
        return accountServiceUrl;
    }
}
