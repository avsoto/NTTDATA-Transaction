package com.bankingSystem.transaction.service.impl;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.MicroServiceClient;
import com.bankingSystem.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MicroServiceClient microserviceClient;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, MicroServiceClient microserviceClient) {
        this.transactionRepository = transactionRepository;
        this.microserviceClient = microserviceClient;
    }

    @Override
    public Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }

        // Validate that the account exists in the Accounts microservice
        return microserviceClient.getAccountDetails(accountId)
                .flatMap(accountDetail -> {
                    BigDecimal currentBalance = accountDetail.getBalance();
                    BigDecimal newBalance =  currentBalance.add(amount);

                    return microserviceClient.updateBalance(accountId, newBalance)
                            .doOnSuccess(response -> System.out.println("Balance successfully updated in MySQL."))
                            .doOnError(error -> System.out.println("Error updating balance:" + error.getMessage()))
                            .then(Mono.defer(() -> {
                                // Create and save the transaction if the update was successful
                                Transaction transaction = new Transaction();
                                transaction.setType("SAVING");
                                transaction.setAmount(amount);
                                transaction.setDate(LocalDateTime.now());
                                transaction.setOriginAccount(accountDetail.getAccountNumber());
                                transaction.setDestinationAccount(null);

                                // Save transaction to MongoDB
                                return transactionRepository.save(transaction)
                                        .doOnSuccess(savedTransaction -> {
                                            System.out.println("Save transaction: " + savedTransaction);
                                        })
                                        .doOnError(e -> {
                                            System.err.println("Error saving transaction: " + e.getMessage());
                                        });                            }));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The destination account does not exist")));
    }

    @Override
    public Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }

        // Validate that the account exists in the Accounts microservice
        return microserviceClient.getAccountDetails(accountId)
                .flatMap(accountDetail -> {
                    BigDecimal currentBalance = accountDetail.getBalance();

                    // Check if the balance is sufficient for withdrawal
                    if (currentBalance.compareTo(amount) < 0) {
                        return Mono.error(new IllegalArgumentException("Insufficient balance."));
                    }

                    // Calculate the new balance after withdrawal
                    BigDecimal newBalance = currentBalance.subtract(amount);

                    return microserviceClient.updateBalance(accountId, newBalance)
                            .doOnSuccess(response -> System.out.println("Balance successfully updated in MySQL."))
                            .doOnError(error -> System.out.println("Error updating balance: " + error.getMessage()))
                            .then(Mono.defer(() -> {
                                // Create and save the transaction if the update was successful
                                Transaction transaction = new Transaction();
                                transaction.setType("WITHDRAWAL");
                                transaction.setAmount(amount);
                                transaction.setDate(LocalDateTime.now());
                                transaction.setOriginAccount(accountDetail.getAccountNumber());
                                transaction.setDestinationAccount(null); // Can be null if the withdrawal does not have a specific destination

                                // Save the transaction to MongoDB
                                return transactionRepository.save(transaction)
                                        .doOnSuccess(savedTransaction -> {
                                            System.out.println("Save transaction: " + savedTransaction);
                                        })
                                        .doOnError(e -> {
                                            System.err.println("Error saving transaction: " + e.getMessage());
                                        });
                            }));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The account does not exist.")));

    }

    @Override
    public Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        // Validate that the amount is greater than zero
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be greater than 0"));
        }

        // Get the source account
        return microserviceClient.getAccountDetails(sourceAccountId)
                .flatMap(sourceAccount -> {
                    // Get the target account
                    return microserviceClient.getAccountDetails(destinationAccountId)
                            .flatMap(destinationAccount -> {
                                // Check if the source account has sufficient balance
                                if (sourceAccount.getBalance().compareTo(amount) < 0) {
                                    return Mono.error(new IllegalArgumentException("Insufficient balance in the source account"));
                                }

                                // Subtract the amount from the source account
                                BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
                                // Add the amount to the destination account
                                BigDecimal newDestinationBalance = destinationAccount.getBalance().add(amount);

                                // Update accounts
                                return microserviceClient.updateBalance(sourceAccountId, newSourceBalance)
                                        .then(microserviceClient.updateBalance(destinationAccountId, newDestinationBalance))
                                        .then(Mono.defer(() -> {
                                            // Create and save the transaction
                                            Transaction transaction = new Transaction();
                                            transaction.setType("TRANSFER");
                                            transaction.setAmount(amount);
                                            transaction.setDate(LocalDateTime.now());
                                            transaction.setOriginAccount(sourceAccount.getAccountNumber());
                                            transaction.setDestinationAccount(destinationAccount.getAccountNumber());

                                            return transactionRepository.save(transaction);
                                        }));
                            });
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("One or both accounts do not exist")));

    }

    @Override
    public Mono<List<Transaction>> getTransactionHistory() {
        return null;
    }
}
