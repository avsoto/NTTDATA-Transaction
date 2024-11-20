package com.bankingSystem.transaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WithdrawalRequest {
    private BigDecimal amount;
}