package com.bankingSystem.transaction.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountDTO {
    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private Integer customerId;
}