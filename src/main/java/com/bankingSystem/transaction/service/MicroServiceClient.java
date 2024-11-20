package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.BankAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MicroServiceClient {

    private final WebClient webClient;

    @Value("${microservice.accounts.url}")
    private String accountServiceUrl;

    @Value("${microservice.customers.url}")
    private String customerServiceUrl;

    public MicroServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BankAccount> getAccountDetails(Integer accountId) {
        return webClient.get()
                .uri(accountServiceUrl + "/{accountId}", accountId)
                .retrieve()
                .bodyToMono(BankAccount.class)
                .doOnNext(account -> System.out.println("Account received: " + account))
                .doOnError(e -> System.err.println("Error getting account: " + e.getMessage()));
    }

    public Mono<String> getCustomerDetails(String customerId) {
        return webClient
                .get()
                .uri(customerServiceUrl + "{id}", customerId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Void> updateBalance(Integer accountId, BigDecimal newBalance) {
        return webClient.put()
                .uri(accountServiceUrl + "/{accountId}/balance", accountId)
                .bodyValue(Map.of("balance", newBalance))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
