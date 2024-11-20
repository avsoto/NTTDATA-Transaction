package com.bankingSystem.transaction.service.utils;

import com.bankingSystem.transaction.model.BankAccount;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.MicroServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionValidationService {

    private final TransactionRepository transactionRepository;
    private final MicroServiceClient microserviceClient;

    public Mono<Void> validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }
        return Mono.empty();
    }

    public Mono<BankAccount> validateAccount(Integer accountId) {
        return microserviceClient.getAccountDetails(accountId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The account does not exist.")));
    }

    public Mono<Transaction> createTransactionAndSave(TransactionRepository transactionRepository, Transaction transaction) {
        return transactionRepository.save(transaction)
                .doOnSuccess(savedTransaction -> System.out.println("Save transaction: " + savedTransaction))
                .doOnError(e -> System.err.println("Error saving transaction: " + e.getMessage()));
    }


    public Mono<Transaction> updateBalanceAndCreateTransaction(Integer accountId, BigDecimal newBalance, TransactionType transactionType,
                                                               BigDecimal amount, String originAccount, String destinationAccount) {
        return microserviceClient.updateBalance(accountId, newBalance)
                .doOnSuccess(response -> System.out.println("Balance successfully updated"))
                .doOnError(error -> System.out.println("Error updating balance:" + error.getMessage()))
                .then(Mono.defer(() -> {
                    Transaction transaction = new Transaction();
                    transaction.setType(transactionType);
                    transaction.setAmount(amount);
                    transaction.setDate(LocalDateTime.now());
                    transaction.setOriginAccount(originAccount);
                    transaction.setDestinationAccount(destinationAccount);

                    return createTransactionAndSave(transactionRepository, transaction);
                }));
    }



}
