package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.exception.AccountNotFoundException;
import com.bankingSystem.transaction.model.BankAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AccountServiceClient {

    private final WebClient webClient;

    @Value("${microservice.accounts.url}")
    private String accountServiceUrl;

    public AccountServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BankAccount> getAccountDetails(Integer accountId) {
        return webClient.get()
                .uri(accountServiceUrl + "/{accountId}", accountId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> {
                            if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.error(new AccountNotFoundException("Account with ID " + accountId + " not found."));
                            }
                            return Mono.error(new RuntimeException("Account service error: " + clientResponse.statusCode()));
                        })
                .bodyToMono(BankAccount.class)
                .doOnNext(account -> System.out.println("Account received: " + account))
                .doOnError(e -> System.err.println("Error getting account: " + e.getMessage()));
    }

    public Mono<Void> updateBalance(Integer accountId, BigDecimal newBalance) {
        return webClient.put()
                .uri(accountServiceUrl + "/{accountId}/balance", accountId)
                .bodyValue(Map.of("balance", newBalance))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
