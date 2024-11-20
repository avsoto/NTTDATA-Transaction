package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.model.Transaction;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount);

    Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount);

    Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount);

    Mono<List<Transaction>> getTransactionHistory();

}
