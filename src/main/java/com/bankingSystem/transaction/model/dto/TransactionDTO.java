package com.bankingSystem.transaction.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a transaction.
 * This class is used to transfer transaction details between different layers of the application.
 */
@Data
@AllArgsConstructor
public class TransactionDTO {

    private String id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String originAccount;
    private String destinationAccount;
}
