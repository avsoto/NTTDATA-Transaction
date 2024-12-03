package com.bankingSystem.transaction.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Utility class for common transaction-related operations.
 */
@Component
@AllArgsConstructor
public class TransactionUtil {

    /**
     * Validates if the provided amount is greater than zero.
     * @return a {@link Mono} that completes if the amount is valid, or emits an {@link IllegalArgumentException} if the amount is invalid.
     */
    public Mono<Void> validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }
        return Mono.empty();
    }
}
