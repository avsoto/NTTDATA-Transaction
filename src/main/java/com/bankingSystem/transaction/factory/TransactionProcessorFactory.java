package com.bankingSystem.transaction.factory;

import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.processor.DepositProcessor;
import com.bankingSystem.transaction.processor.TransactionProcessor;
import com.bankingSystem.transaction.processor.TransferProcessor;
import com.bankingSystem.transaction.processor.WithdrawalProcessor;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.service.util.TransactionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionProcessorFactory {

    private final TransactionRepository transactionRepository;
    private final TransactionUtil validationService;
    private final AccountServiceClient microServiceClient;

    public TransactionProcessor getProcessor(TransactionType type) {
        return switch (type) {
            case SAVING -> new DepositProcessor(transactionRepository, validationService, microServiceClient);
            case WITHDRAWAL -> new WithdrawalProcessor(transactionRepository, validationService, microServiceClient);
            case TRANSFER -> new TransferProcessor(transactionRepository, validationService, microServiceClient);
        };
    }
}
