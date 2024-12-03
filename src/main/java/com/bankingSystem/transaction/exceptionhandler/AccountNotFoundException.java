package com.bankingSystem.transaction.exceptionhandler;

/**
 * Exception thrown when an account is not found.
 * This exception is typically used to indicate that an account with the specified
 * identifier does not exist in the system.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code AccountNotFoundException} with the specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public AccountNotFoundException(String message) {
        super(message);
    }
}

