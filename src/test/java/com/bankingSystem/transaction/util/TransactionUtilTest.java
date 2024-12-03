package com.bankingSystem.transaction.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class TransactionUtilTest {

    @InjectMocks
    private final TransactionUtil transactionUtil = new TransactionUtil();

    @Test
    /*
    * Verifies that validating an amount greater than zero completes successfully without errors.
    * */
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
    /*
    * Verifies that validating an amount equal to zero throws an IllegalArgumentException with the appropriate error message.
    * */
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
    /*
    * Verifies that validating an amount less than zero throws an IllegalArgumentException with the appropriate error message.
    * */
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
