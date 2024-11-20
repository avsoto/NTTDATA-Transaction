package com.bankingSystem.transaction.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccount {
    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private Integer customerId;
}