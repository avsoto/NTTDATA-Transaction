package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract class responsible for processing different types of transactions.
 * This class provides common functionality for processing transactions, such as creating and saving
 * transaction records. Specific transaction types (e.g., deposit, withdrawal, transfer) should
 * implement the {@link #processTransaction(Integer, BigDecimal)} method with the respective logic.
 */
@Component
@AllArgsConstructor
public abstract class TransactionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    protected final TransactionUtil transactionUtil;
    protected final AccountServiceClient accountServiceClient;
    protected final TransactionRepository transactionRepository;

    /**
     * Abstract method to process a specific type of transaction.
     * @return The processed transaction.
     */
    public abstract Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount);

    /**
     * Creates a transaction object and saves it to the repository.
     * @return The saved transaction.
     */
    public Mono<Transaction> createTransaction(Integer accountId, BigDecimal balance, BigDecimal amount, TransactionType type, String originAccount, String destinationAccount) {
        Transaction transaction = new Transaction(null, type, amount, LocalDateTime.now(), originAccount, destinationAccount);
        return saveTransaction(transaction);
    }

    /**
     * Saves the transaction to the repository.
     * @return The saved transaction.
     */

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction)
                .doOnSubscribe(subscription -> System.out.println("Starting to save transaction"))
                .doOnNext(savedTransaction -> System.out.println("Transaction saved: " + savedTransaction))
                .doOnError(e -> System.err.println("Error saving transaction: " + e.getMessage()))
                .doOnTerminate(() -> System.out.println("Save transaction completed"))
                .doOnSuccess(savedTransaction -> logger.info("{} transaction saved successfully: {}", transaction.getType(), savedTransaction));
    }
}
