package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.exceptionhandler.InsufficientBalanceException;
import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Processor responsible for handling and processing transfer transactions.
 * This class provides the logic for transferring funds between two accounts. It validates the transfer amount,
 * checks the source account balance, calculates the new balances for both the source and destination accounts,
 * updates the balances, and creates a transaction record in the repository.
 */
public class TransferProcessor extends TransactionProcessor {

    /**
     * Constructor for the TransferProcessor.
     */
    public TransferProcessor(TransactionRepository transactionRepository,
                             TransactionUtil transactionUtil,
                             AccountServiceClient accountServiceClient) {
        super(transactionUtil, accountServiceClient, transactionRepository);
    }

    /**
     * This method is a placeholder and is not used directly in the TransferProcessor.
     * It is designed to throw an exception if called, indicating that transfers should be processed using {@link #processTransfer(Integer, Integer, BigDecimal)}.
     * @return A {@link Mono} that throws an {@link UnsupportedOperationException}.
     */
    @Override
    public Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount) {
    // This method is just a placeholder and is not used directly in Transfer
        return Mono.error(new UnsupportedOperationException("Use processTransfer for transfers"));
    }

    /**
     * Specific method to process a transfer between two accounts.
     * This method validates the transfer amount, retrieves the account details for both the source and destination
     * accounts, verifies if the source account has sufficient funds, calculates the new balances for both accounts,
     * and then updates the balances. Finally, a transaction record is created and saved.
     * @return A {@link Mono} containing the saved transaction, or an error if any step fails.
     */
    public Mono<Transaction> processTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        return validateTransferAmount(amount)
                .then(getAccountDetails(sourceAccountId))
                .flatMap(sourceAccount ->
                        getAccountDetails(destinationAccountId)
                                .flatMap(destinationAccount ->
                                        verifySufficientBalance(sourceAccount, amount)
                                                .then(Mono.defer(() -> {
                                                    BigDecimal newSourceBalance = calculateNewBalance(sourceAccount.getBalance(), amount, false);
                                                    BigDecimal newDestinationBalance = calculateNewBalance(destinationAccount.getBalance(), amount, true);
                                                    return updateBalancesAndProcessTransfer(sourceAccountId, destinationAccountId,
                                                            newSourceBalance, newDestinationBalance, amount, sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber());
                                                }))
                                )
                );
    }

    /**
     * Validates the transfer amount.
     * This method checks if the amount is valid (e.g., not null and positive).
     * @return A {@link Mono} that completes successfully if the amount is valid, or an error if invalid.
     */
    public Mono<Void> validateTransferAmount(BigDecimal amount) {
        return transactionUtil.validateAmount(amount);
    }

    /**
     * Fetches the details of a bank account by its ID.
     * This method retrieves the details of a bank account from the account service using the provided account ID.
     * @return A {@link Mono} containing the {@link BankAccountDTO} with the account details.
     */
    public Mono<BankAccountDTO> getAccountDetails(Integer accountId) {
        return accountServiceClient.fetchBankAccountById(accountId);
    }

    /**
     * Verifies if the source account has sufficient balance for the transfer.
     * This method checks if the source account has enough funds to complete the transfer. If the balance is insufficient,
     * it throws an {@link InsufficientBalanceException}.
     * @return A {@link Mono} that completes successfully if there is sufficient balance, or an error if not.
     */
    public Mono<Void> verifySufficientBalance(BankAccountDTO sourceAccount, BigDecimal amount) {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            return Mono.error(new InsufficientBalanceException("Insufficient balance in source account"));
        }
        return Mono.empty();
    }

    /**
     * Calculates the new balance for an account after a deposit or withdrawal.
     * This method either adds or subtracts the transfer amount from the current balance depending on whether the
     * operation is a deposit or a withdrawal.
     * @return The new balance of the account.
     */
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, boolean isDeposit) {
        return isDeposit ? currentBalance.add(amount) : currentBalance.subtract(amount);
    }

    /**
     * Updates the balances of the source and destination accounts and processes the transfer transaction.
     * This method adjusts the balances of both accounts, saves the transaction record, and logs the outcome.
     * @return A {@link Mono} containing the saved transaction, or an error if any step fails.
     */
    public Mono<Transaction> updateBalancesAndProcessTransfer(Integer sourceAccountId, Integer destinationAccountId,
                                                               BigDecimal newSourceBalance, BigDecimal newDestinationBalance,
                                                               BigDecimal amount, String originAccount, String destinationAccount) {
        return accountServiceClient.adjustBankAccountBalance(sourceAccountId, newSourceBalance)
                .then(accountServiceClient.adjustBankAccountBalance(destinationAccountId, newDestinationBalance))
                .then(createTransaction(sourceAccountId, newSourceBalance, amount, TransactionType.TRANSFER, originAccount, destinationAccount))
                .doOnSuccess(savedTransaction -> System.out.println("Transfer transaction saved successfully"))
                .doOnError(e -> System.err.println("Error saving transfer transaction: " + e.getMessage()));
    }
}
