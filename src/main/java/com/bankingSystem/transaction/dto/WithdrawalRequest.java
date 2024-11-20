package com.bankingSystem.transaction.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Getter
@Setter
public class WithdrawalRequest {
    private BigDecimal amount;
}
