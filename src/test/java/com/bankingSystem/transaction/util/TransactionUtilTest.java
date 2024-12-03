package com.bankingSystem.transaction.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionUtilTest {

    @InjectMocks
    private final TransactionUtil transactionUtil = new TransactionUtil();

    @Test
    void validateAmount_AmountGreaterThanZero_CompletesSuccessfully() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100);

        // Act
        Mono<Void> result = transactionUtil.validateAmount(amount);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validateAmount_AmountEqualToZero_ThrowsIllegalArgumentException() {
        // Arrange
        BigDecimal amount = BigDecimal.ZERO;

        // Act
        Mono<Void> result = transactionUtil.validateAmount(amount);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("The amount must be higher than 0."))
                .verify();
    }

    @Test
    void validateAmount_AmountLessThanZero_ThrowsIllegalArgumentException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(-10);

        // Act
        Mono<Void> result = transactionUtil.validateAmount(amount);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("The amount must be higher than 0."))
                .verify();
    }

}
