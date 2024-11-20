package com.bankingSystem.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "banking")
public class Transaction {

    @Id
    private String id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String originAccount;
    private String destinationAccount;
}
