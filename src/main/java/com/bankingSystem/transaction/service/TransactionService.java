package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for handling banking transactions such as deposits, withdrawals, and transfers.
 * This interface defines methods for registering transactions (deposits, withdrawals, and transfers)
 * and retrieving transaction history.
 */
public interface TransactionService {

    /**
     * Registers a deposit transaction for a specified account.
     * This method processes a deposit for the given account and amount.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount);

    /**
     * Registers a withdrawal transaction for a specified account.
     * This method processes a withdrawal for the given account and amount.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount);

    /**
     * Registers a transfer transaction between two accounts.
     * This method processes a transfer between the source account and the destination account for the given amount.
     * @return A {@link Mono} containing the processed {@link Transaction}.
     */
    Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount);

    /**
     * Retrieves the transaction history of all transactions.
     * This method fetches a list of all transactions in the system.
     * @return A {@link Mono} containing a list of {@link Transaction} objects.
     */
    Mono<List<Transaction>> getTransactionHistory();

}
