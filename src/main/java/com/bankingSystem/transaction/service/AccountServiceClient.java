package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import com.bankingSystem.transaction.exceptionhandler.AccountServiceErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient webClient;
    private final AccountServiceErrorHandler errorHandler;
    private final String accountServiceUrl;

    public Mono<BankAccountDTO> fetchBankAccountById(Integer accountId) {
        return webClient.get()
                .uri(accountServiceUrl + "/{accountId}", accountId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> errorHandler.handleAccountError(clientResponse, accountId)
                )
                .bodyToMono(BankAccountDTO.class)
                .doOnNext(account -> System.out.println("Account received: " + account))
                .doOnError(e -> System.err.println("Error getting account: " + e.getMessage()));
    }

    public Mono<Void> adjustBankAccountBalance(Integer accountId, BigDecimal newBalance) {
        return webClient.put()
                .uri(accountServiceUrl + "/{accountId}/balance", accountId)
                .bodyValue(Map.of("balance", newBalance))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
