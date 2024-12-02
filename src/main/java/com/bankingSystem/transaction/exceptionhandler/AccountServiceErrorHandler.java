package com.bankingSystem.transaction.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Component
public class AccountServiceErrorHandler {

    public Mono<Throwable> handleAccountError(ClientResponse clientResponse, Integer accountId) {
        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
            return Mono.error(new AccountNotFoundException("Account with ID " + accountId + " not found."));
        }

        if (clientResponse.statusCode().is4xxClientError()) {
            return Mono.error(new RuntimeException("Client error occurred while fetching account details for ID " + accountId));
        } else if (clientResponse.statusCode().is5xxServerError()) {
            return Mono.error(new RuntimeException("Server error occurred while fetching account details for ID " + accountId));
        }

        return Mono.error(new RuntimeException("Unexpected error occurred while accessing account service."));
    }
}

