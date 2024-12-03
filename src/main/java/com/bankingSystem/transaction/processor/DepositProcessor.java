package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Processor responsible for handling deposit transactions.
 * This class processes deposit transactions by validating the amount, fetching the account details,
 * adjusting the account balance, and creating a transaction record.
 */
public class DepositProcessor extends TransactionProcessor {

    /**
     * Constructs a {@link DepositProcessor} with the necessary dependencies.
     * @param transactionRepository the repository for transaction persistence.
     * @param transactionUtil the utility class for transaction-related operations.
     * @param accountServiceClient the client to interact with the account service.
     */
    public DepositProcessor(TransactionRepository transactionRepository,
                            TransactionUtil transactionUtil,
                            AccountServiceClient accountServiceClient) {
        super(transactionUtil, accountServiceClient, transactionRepository);
    }

    /**
     * Processes the deposit transaction.
     * Validates the deposit amount, retrieves the account information, updates the account balance,
     * and creates a new transaction record.
     * @return a {@link Mono} containing the created transaction once the process is complete.
     */
    @Override
    public Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount) {
        return transactionUtil.validateAmount(amount)
                .then(accountServiceClient.fetchBankAccountById(accountId))
                .flatMap(account -> {
                    BigDecimal newBalance = account.getBalance().add(amount);
                    return accountServiceClient.adjustBankAccountBalance(accountId, newBalance)
                            .then(createTransaction(accountId, newBalance, amount, TransactionType.SAVING, account.getAccountNumber(), null));
                });
    }
}
