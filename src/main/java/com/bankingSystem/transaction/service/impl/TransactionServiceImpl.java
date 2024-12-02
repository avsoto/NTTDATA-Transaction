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


@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionProcessorFactory transactionProcessorFactory;
    private final TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount) {
        //A DepositProcessor (which implements TransactionProcessor) is instantiated and returned.
        TransactionProcessor processor = transactionProcessorFactory.getProcessor(TransactionType.SAVING);
        return processor.process(accountId, amount);
    }

    @Override
    public Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount) {
        TransactionProcessor processor = transactionProcessorFactory.getProcessor(TransactionType.WITHDRAWAL);
        return processor.process(accountId, amount);
    }

    @Override
    public Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        TransactionProcessor processor = transactionProcessorFactory.getProcessor(TransactionType.TRANSFER);
        return ((TransferProcessor) processor).processTransfer(sourceAccountId, destinationAccountId, amount);
    }

    @Override
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionRepository.findAll()
                .collectList();
    }
}
