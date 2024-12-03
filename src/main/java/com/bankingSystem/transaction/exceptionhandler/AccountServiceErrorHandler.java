package com.bankingSystem.transaction.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * Handles errors that occur while interacting with the account service.
 * This class provides a mechanism to map HTTP response statuses from the account service
 * to appropriate exceptions for better error handling in the application.
 */
@Component
public class AccountServiceErrorHandler {

    /**
     * Handles errors based on the response received from the account service.
     * @param clientResponse the {@link ClientResponse} received from the account service
     * @param accountId      the ID of the account for which the error occurred
     * @return a {@link Mono} that emits the appropriate exception based on the HTTP status code
     */
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

