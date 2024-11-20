package com.bankingSystem.transaction.service.impl;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.MicroServiceClient;
import com.bankingSystem.transaction.service.TransactionService;
import com.bankingSystem.transaction.service.utils.TransactionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MicroServiceClient microserviceClient;
    private final TransactionValidationService transactionValidationService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, MicroServiceClient microserviceClient, TransactionValidationService transactionValidationService) {
        this.transactionRepository = transactionRepository;
        this.microserviceClient = microserviceClient;
        this.transactionValidationService = transactionValidationService;
    }

    @Override
    public Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount) {
        return transactionValidationService.validateAmount(amount)
                .then(transactionValidationService.validateAccount(accountId))
                .flatMap(accountDetail -> {
                    BigDecimal currentBalance = accountDetail.getBalance();
                    BigDecimal newBalance = currentBalance.add(amount);

                    return transactionValidationService.updateBalanceAndCreateTransaction(
                            accountId,
                            newBalance,
                            TransactionType.SAVING,
                            amount,
                            accountDetail.getAccountNumber(),
                            null);
                });
    }

    @Override
    public Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount) {
        return transactionValidationService.validateAmount(amount)
                    .then(microserviceClient.getAccountDetails(accountId)
                            .flatMap(accountDetail -> {
                                BigDecimal currentBalance = accountDetail.getBalance();

                                return transactionValidationService.validateAmount(amount)
                                        .then(Mono.defer(() -> {
                                            if (currentBalance.compareTo(amount) < 0) {
                                                return Mono.error(new IllegalArgumentException("Insufficient balance."));
                                            }

                                            BigDecimal newBalance = currentBalance.subtract(amount);

                                            return transactionValidationService.updateBalanceAndCreateTransaction(
                                                            accountId,
                                                            newBalance,
                                                            TransactionType.WITHDRAWAL,
                                                            amount,
                                                            accountDetail.getAccountNumber(),
                                                            null)
                                                    .doOnSuccess(savedTransaction -> System.out.println("Transaction registered successfully."));
                                        }));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                System.out.println("Account with ID " + accountId + " does not exist.");
                                return Mono.error(new IllegalArgumentException("The account does not exist."));
                            }))

                    );
    }

    @Override
    public Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        return transactionValidationService.validateAmount(amount)
                .then(microserviceClient.getAccountDetails(sourceAccountId)
                        .flatMap(sourceAccount -> {
                            return microserviceClient.getAccountDetails(destinationAccountId)
                                    .flatMap(destinationAccount -> {
                                        return transactionValidationService.validateAmount(amount)
                                                .then(Mono.defer(() -> {
                                                    if (sourceAccount.getBalance().compareTo(amount) < 0) {
                                                        return Mono.error(new IllegalArgumentException("Insufficient balance in the source account"));
                                                    }

                                                    BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
                                                    BigDecimal newDestinationBalance = destinationAccount.getBalance().add(amount);

                                                    return transactionValidationService.updateBalanceAndCreateTransaction(
                                                                    sourceAccountId,
                                                                    newSourceBalance,
                                                                    TransactionType.TRANSFER,
                                                                    amount,
                                                                    sourceAccount.getAccountNumber(),
                                                                    destinationAccount.getAccountNumber())
                                                            .then(transactionValidationService.updateBalanceAndCreateTransaction(
                                                                    destinationAccountId,
                                                                    newDestinationBalance,
                                                                    TransactionType.TRANSFER,
                                                                    amount,
                                                                    sourceAccount.getAccountNumber(),
                                                                    destinationAccount.getAccountNumber()))
                                                            .doOnSuccess(savedTransaction -> System.out.println("Transfer transaction registered successfully."));
                                                }));
                                    });
                        })
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("One or both accounts do not exist"))));

    }

    @Override
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionRepository.findAll()
                .collectList();
    }
}
