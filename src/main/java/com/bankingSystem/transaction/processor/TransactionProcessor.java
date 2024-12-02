package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public abstract class TransactionProcessor {

    protected final TransactionUtil transactionUtil;
    protected final AccountServiceClient accountServiceClient;
    protected final TransactionRepository transactionRepository;

    public abstract Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount);

    protected Mono<Transaction> createTransaction(Integer accountId, BigDecimal balance, BigDecimal amount, TransactionType type, String originAccount, String destinationAccount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(LocalDateTime.now());
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);

        return saveTransaction(transaction);
    }

    private Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction)
                .doOnSuccess(savedTransaction -> {
                    System.out.println("Save transaction: " + savedTransaction);
                    System.out.println(transaction.getType() + " transaction saved successfully");

                })
                .doOnError(e -> {
                    System.err.println("Error saving transaction: " + e.getMessage());
                });
    }
}
