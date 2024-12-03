package com.bankingSystem.transaction.service.impl;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.TransactionService;
import com.bankingSystem.transaction.processor.TransactionProcessor;
import com.bankingSystem.transaction.factory.TransactionProcessorFactory;
import com.bankingSystem.transaction.processor.TransferProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the {@link TransactionService} interface.
 * This service handles the processing of different types of transactions, including deposits,
 * withdrawals, and transfers. It uses the {@link TransactionProcessorFactory} to create specific
 * processors for each type of transaction and interacts with the {@link TransactionRepository}
 * to save and retrieve transactions.
 */
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionProcessorFactory transactionProcessorFactory;
    private final TransactionRepository transactionRepository;

    /**
     * Registers a deposit transaction.
     * This method processes a deposit by validating the amount and updating the account balance.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    @Override
    public Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount) {
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(TransactionType.SAVING);
        return processor.processTransaction(accountId, amount);
    }

    /**
     * Registers a withdrawal transaction.
     * This method processes a withdrawal by validating the amount and ensuring sufficient balance.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    @Override
    public Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount) {
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(TransactionType.WITHDRAWAL);
        return processor.processTransaction(accountId, amount);
    }

    /**
     * Registers a transfer transaction.
     * This method processes a transfer between two accounts by validating the amount and ensuring
     * sufficient balance in the source account.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    @Override
    public Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(TransactionType.TRANSFER);
        return ((TransferProcessor) processor).processTransfer(sourceAccountId, destinationAccountId, amount);
    }

    /**
     * Retrieves the transaction history.
     * This method fetches all transactions from the repository and returns them as a list.
     * @return A {@link Mono} containing a list of all {@link Transaction} objects.
     */
    @Override
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionRepository.findAll()
                .collectList();
    }
}
