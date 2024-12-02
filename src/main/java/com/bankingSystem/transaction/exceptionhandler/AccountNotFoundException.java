package com.bankingSystem.transaction.exceptionhandler;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
