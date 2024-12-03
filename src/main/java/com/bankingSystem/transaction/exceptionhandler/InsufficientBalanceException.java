package com.bankingSystem.transaction.exceptionhandler;

/**
 * Exception thrown when an account has insufficient balance to complete a transaction.
 * This exception is typically used to indicate that the requested transaction
 * cannot be processed due to a lack of funds in the account.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs a new {@code InsufficientBalanceException} with the specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

