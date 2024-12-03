package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import com.bankingSystem.transaction.exceptionhandler.AccountServiceErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Client for interacting with the account service to retrieve account details and adjust balances.
 * This service communicates with the account service using {@link WebClient} to fetch account information
 * and update account balances. It also handles errors using the {@link AccountServiceErrorHandler}.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient webClient;
    private final AccountServiceErrorHandler errorHandler;
    private final String accountServiceUrl;

    /**
     * Fetches the bank account details by account ID.
     * This method makes a GET request to the account service to retrieve the details of the account
     * corresponding to the provided account ID.
     * @return A {@link Mono} containing the {@link BankAccountDTO} with account details.
     */
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

    /**
     * Adjusts the balance of a specific bank account.
     * This method makes a PUT request to update the balance of the bank account with the specified account ID.
     * @return A {@link Mono} representing the completion of the update.
     */
    public Mono<Void> adjustBankAccountBalance(Integer accountId, BigDecimal newBalance) {
        return webClient.put()
                .uri(accountServiceUrl + "/{accountId}/balance", accountId)
                .bodyValue(Map.of("balance", newBalance))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
