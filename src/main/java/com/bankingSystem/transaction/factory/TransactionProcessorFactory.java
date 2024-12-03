package com.bankingSystem.transaction.factory;

import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.processor.DepositProcessor;
import com.bankingSystem.transaction.processor.TransactionProcessor;
import com.bankingSystem.transaction.processor.TransferProcessor;
import com.bankingSystem.transaction.processor.WithdrawalProcessor;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating instances of {@link TransactionProcessor} based on the transaction type.
 * This factory decouples the creation of transaction processors, providing a centralized way
 * to instantiate the appropriate processor for handling different transaction types.
 */
@Component
@AllArgsConstructor
public class TransactionProcessorFactory {

    private final TransactionRepository transactionRepository;
    private final TransactionUtil transactionUtil;
    private final AccountServiceClient accountServiceClient;

    /**
     * Builds a {@link TransactionProcessor} for the specified {@link TransactionType}.
     * @return an instance of the corresponding {@link TransactionProcessor}
     * @throws IllegalArgumentException if the transaction type is not supported
     */
    public TransactionProcessor buildTransactionProcessor(TransactionType type) {
        return switch (type) {
            case SAVING -> new DepositProcessor(transactionRepository, transactionUtil, accountServiceClient);
            case WITHDRAWAL -> new WithdrawalProcessor(transactionRepository, transactionUtil, accountServiceClient);
            case TRANSFER -> new TransferProcessor(transactionRepository, transactionUtil, accountServiceClient);
        };
    }
}
