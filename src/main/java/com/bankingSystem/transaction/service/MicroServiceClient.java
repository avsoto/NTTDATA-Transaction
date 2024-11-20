package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.BankAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
                .onStatus(
                        status -> status.is4xxClientError(), // Expresión lambda que llama al método no estático
                        clientResponse -> {
                            // Si el servicio devuelve un código 404 (no encontrado), retornamos Mono.empty()
                            if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.empty();  // Esto hará que el flujo entre en switchIfEmpty()
                            }
                            return Mono.error(new RuntimeException("Account service error: " + clientResponse.statusCode()));
                        })
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
