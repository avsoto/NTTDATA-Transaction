package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class WithdrawalProcessor extends TransactionProcessor {

    public WithdrawalProcessor(TransactionRepository transactionRepository,
                               TransactionUtil transactionUtil,
                               AccountServiceClient accountServiceClient) {
        super(transactionUtil, accountServiceClient, transactionRepository);
    }

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
