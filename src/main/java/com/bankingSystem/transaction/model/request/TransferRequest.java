package com.bankingSystem.transaction.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Request object representing the details of a transfer operation between accounts.
 * This class is used to capture and validate input data for transfer transactions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Source Account Id cannot be null")
    private Integer sourceAccountId;

    @NotNull(message = "Destination Account Id cannot be null")
    private Integer destinationAccountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
