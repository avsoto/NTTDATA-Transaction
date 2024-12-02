package com.bankingSystem.transaction.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
