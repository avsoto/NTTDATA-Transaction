package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Processor responsible for handling and processing withdrawal transactions.
 * This class provides the logic for withdrawing funds from an account. It validates the withdrawal amount,
 * checks if the account has sufficient balance, calculates the new balance, updates the account balance,
 * and creates a transaction record in the repository.
 */
public class WithdrawalProcessor extends TransactionProcessor {

    /**
     * Constructor for the WithdrawalProcessor.
     * @param transactionRepository The transaction repository used to save transaction records.
     * @param transactionUtil Utility class for validating amounts and other transaction-related operations.
     * @param accountServiceClient Client for interacting with the account service to fetch and adjust bank account details.
     */
    public WithdrawalProcessor(TransactionRepository transactionRepository,
                               TransactionUtil transactionUtil,
                               AccountServiceClient accountServiceClient) {
        super(transactionUtil, accountServiceClient, transactionRepository);
    }

    /**
     * Processes a withdrawal transaction.
     * This method validates the withdrawal amount, retrieves the account details, checks if the account has sufficient
     * balance to complete the withdrawal, calculates the new balance, and then updates the account balance. Finally,
     * a transaction record is created and saved.
     * @return A {@link Mono} containing the saved transaction, or an error if any step fails.
     */
    @Override
    public Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount) {
        return transactionUtil.validateAmount(amount)
                .then(accountServiceClient.fetchBankAccountById(accountId))
                .flatMap(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new IllegalArgumentException("Insufficient balance"));
                    }

                    BigDecimal newBalance = account.getBalance().subtract(amount);
                    return accountServiceClient.adjustBankAccountBalance(accountId, newBalance)
                            .then(createTransaction(accountId, newBalance, amount, TransactionType.WITHDRAWAL, account.getAccountNumber(), null));
                });
    }
}
