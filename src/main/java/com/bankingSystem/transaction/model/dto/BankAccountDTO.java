package com.bankingSystem.transaction.model.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a bank account.
 * This class is used to transfer bank account data between different layers of the application.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDTO {

    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private Integer customerId;
}
