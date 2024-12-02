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

public class TransferProcessor extends TransactionProcessor {

    public TransferProcessor(TransactionRepository transactionRepository,
                             TransactionUtil transactionUtil,
                             AccountServiceClient accountServiceClient) {
        super(transactionUtil, accountServiceClient, transactionRepository);
    }

    @Override
    public Mono<Transaction> processTransaction(Integer accountId, BigDecimal amount) {
    // This method is just a placeholder and is not used directly in Transfer
        return Mono.error(new UnsupportedOperationException("Use processTransfer for transfers"));
    }

    //Specific method to process the transfer
    public Mono<Transaction> processTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        return validateTransferAmount(amount)
                .then(getAccountDetails(sourceAccountId))
                .flatMap(sourceAccount ->
                        getAccountDetails(destinationAccountId)
                                .flatMap(destinationAccount ->
                                        verifySufficientBalance(sourceAccount, amount)
                                                .then(Mono.defer(() -> { // Mono.defer se asegura de que la operación se ejecute después
                                                    BigDecimal newSourceBalance = calculateNewBalance(sourceAccount.getBalance(), amount, false);
                                                    BigDecimal newDestinationBalance = calculateNewBalance(destinationAccount.getBalance(), amount, true);
                                                    return updateBalancesAndProcessTransfer(sourceAccountId, destinationAccountId,
                                                            newSourceBalance, newDestinationBalance, amount, sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber());
                                                }))
                                )
                );
    }

    private Mono<Void> validateTransferAmount(BigDecimal amount) {
        return transactionUtil.validateAmount(amount);
    }

    private Mono<BankAccountDTO> getAccountDetails(Integer accountId) {
        return accountServiceClient.fetchBankAccountById(accountId);
    }

    private Mono<Void> verifySufficientBalance(BankAccountDTO sourceAccount, BigDecimal amount) {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            return Mono.error(new InsufficientBalanceException("Insufficient balance in source account"));
        }
        return Mono.empty(); // Si el balance es suficiente, no ocurre nada
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, boolean isDeposit) {
        return isDeposit ? currentBalance.add(amount) : currentBalance.subtract(amount);
    }

    private Mono<Transaction> updateBalancesAndProcessTransfer(Integer sourceAccountId, Integer destinationAccountId,
                                                               BigDecimal newSourceBalance, BigDecimal newDestinationBalance,
                                                               BigDecimal amount, String originAccount, String destinationAccount) {
        return accountServiceClient.adjustBankAccountBalance(sourceAccountId, newSourceBalance)  // Actualiza el balance de la cuenta origen
                .then(accountServiceClient.adjustBankAccountBalance(destinationAccountId, newDestinationBalance))  // Actualiza el balance de la cuenta destino
                .then(createTransaction(sourceAccountId, newSourceBalance, amount, TransactionType.TRANSFER, originAccount, destinationAccount))  // Crea y guarda la transacción
                .doOnSuccess(savedTransaction -> System.out.println("Transfer transaction saved successfully"))
                .doOnError(e -> System.err.println("Error saving transfer transaction: " + e.getMessage()));
    }
}
