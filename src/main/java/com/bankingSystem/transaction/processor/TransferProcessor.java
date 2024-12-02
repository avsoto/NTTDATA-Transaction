package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.service.util.TransactionUtil;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class TransferProcessor extends TransactionProcessor {

    public TransferProcessor(TransactionRepository transactionRepository,
                             TransactionUtil validationService,
                             AccountServiceClient microServiceClient) {
        super(validationService, microServiceClient, transactionRepository);
    }

    @Override
    public Mono<Transaction> process(Integer accountId, BigDecimal amount) {
    // This method is just a placeholder and is not used directly in Transfer
        return Mono.error(new UnsupportedOperationException("Use processTransfer for transfers"));
    }

    //Specific method to process the transfer
    public Mono<Transaction> processTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        return validationService.validateAmount(amount)
                .then(microServiceClient.getAccountDetails(sourceAccountId))
                .flatMap(sourceAccount ->
                        microServiceClient.getAccountDetails(destinationAccountId)
                                .flatMap(destinationAccount -> {

                                    if (sourceAccount.getBalance().compareTo(amount) < 0) {
                                        return Mono.error(new IllegalArgumentException("Insufficient balance in source account"));
                                    }

                                    BigDecimal newSourceBalance = calculateNewBalance(sourceAccount.getBalance(), amount, false);
                                    BigDecimal newDestinationBalance = calculateNewBalance(destinationAccount.getBalance(), amount, true);

                                    return updateBalancesAndSaveTransfer(sourceAccountId, destinationAccountId,
                                            newSourceBalance, newDestinationBalance, amount, sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber());
                                })
                );
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, boolean isDeposit) {
        return isDeposit ? currentBalance.add(amount) : currentBalance.subtract(amount);
    }

    private Mono<Transaction> updateBalancesAndSaveTransfer(Integer sourceAccountId, Integer destinationAccountId,
                                                               BigDecimal newSourceBalance, BigDecimal newDestinationBalance,
                                                               BigDecimal amount, String originAccount, String destinationAccount) {
        return microServiceClient.updateBalance(sourceAccountId, newSourceBalance)  // Actualiza el balance de la cuenta origen
                .then(microServiceClient.updateBalance(destinationAccountId, newDestinationBalance))  // Actualiza el balance de la cuenta destino
                .then(createTransaction(sourceAccountId, newSourceBalance, amount, TransactionType.TRANSFER, originAccount, destinationAccount))  // Crea y guarda la transacción
                .doOnSuccess(savedTransaction -> System.out.println("Transfer transaction saved successfully"))
                .doOnError(e -> System.err.println("Error saving transfer transaction: " + e.getMessage()));
    }


}
