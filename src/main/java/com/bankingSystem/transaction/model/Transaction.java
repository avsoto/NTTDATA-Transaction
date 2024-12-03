package com.bankingSystem.transaction.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction in the banking system.
 * This class stores the details of a transaction, such as the type of transaction,
 * the amount involved, the accounts related to the transaction, and the transaction date.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "banking")
public class Transaction{

    @Id
    private String id;

    private TransactionType type;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime date;

    @NotNull
    private String originAccount;

    @NotNull
    private String destinationAccount;
}
