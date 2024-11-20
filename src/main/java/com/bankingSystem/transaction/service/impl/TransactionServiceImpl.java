package com.bankingSystem.transaction.service.impl;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.MicroServiceClient;
import com.bankingSystem.transaction.service.TransactionService;
import com.bankingSystem.transaction.service.utils.TransactionValidationService;
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
    private final TransactionValidationService transactionValidationService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, MicroServiceClient microserviceClient, TransactionValidationService transactionValidationService) {
        this.transactionRepository = transactionRepository;
        this.microserviceClient = microserviceClient;
        this.transactionValidationService = transactionValidationService;
    }

    @Override
    public Mono<Transaction> registerDeposit(Integer accountId, BigDecimal amount) {
        // Realiza la validación del monto primero
        return transactionValidationService.validateAmount(amount)
                .then(transactionValidationService.validateAccount(accountId))  // Luego valida la cuenta
                .flatMap(accountDetail -> {
                    // Si la validación fue exitosa, obtiene el saldo actual y calcula el nuevo saldo
                    BigDecimal currentBalance = accountDetail.getBalance();
                    BigDecimal newBalance = currentBalance.add(amount);

                    // Llama a updateBalanceAndCreateTransaction después de la validación y el cálculo del nuevo balance
                    return transactionValidationService.updateBalanceAndCreateTransaction(
                            accountId,
                            newBalance,
                            "SAVING",
                            amount,
                            accountDetail.getAccountNumber(),
                            null);
                });
    }

    @Override
    public Mono<Transaction> registerWithdrawal(Integer accountId, BigDecimal amount) {
            // Validar monto (usando TransactionValidationService)
            return transactionValidationService.validateAmount(amount)
                    .then(microserviceClient.getAccountDetails(accountId)
                            .flatMap(accountDetail -> {
                                BigDecimal currentBalance = accountDetail.getBalance();

                                // Validar si el saldo es suficiente
                                return transactionValidationService.validateAmount(amount)
                                        .then(Mono.defer(() -> {
                                            if (currentBalance.compareTo(amount) < 0) {
                                                return Mono.error(new IllegalArgumentException("Insufficient balance."));
                                            }

                                            // Calcular el nuevo saldo
                                            BigDecimal newBalance = currentBalance.subtract(amount);

                                            // Actualizar el saldo y crear la transacción
                                            return transactionValidationService.updateBalanceAndCreateTransaction(
                                                            accountId,
                                                            newBalance,
                                                            "WITHDRAWAL",
                                                            amount,
                                                            accountDetail.getAccountNumber(),
                                                            null) // Sin cuenta de destino para retiro
                                                    .doOnSuccess(savedTransaction -> System.out.println("Transaction registered successfully."));
                                        }));
                            })
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("The account does not exist."))));

    }

    @Override
    public Mono<Transaction> registerTransfer(Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount) {
        // Validar monto (usando TransactionValidationService)
        return transactionValidationService.validateAmount(amount)
                .then(microserviceClient.getAccountDetails(sourceAccountId)
                        .flatMap(sourceAccount -> {
                            // Validar la existencia de la cuenta de destino
                            return microserviceClient.getAccountDetails(destinationAccountId)
                                    .flatMap(destinationAccount -> {
                                        // Verificar si hay suficiente saldo en la cuenta de origen
                                        return transactionValidationService.validateAmount(amount)
                                                .then(Mono.defer(() -> {
                                                    if (sourceAccount.getBalance().compareTo(amount) < 0) {
                                                        return Mono.error(new IllegalArgumentException("Insufficient balance in the source account"));
                                                    }

                                                    // Calcular los nuevos saldos
                                                    BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
                                                    BigDecimal newDestinationBalance = destinationAccount.getBalance().add(amount);

                                                    // Actualizar los saldos de ambas cuentas
                                                    return transactionValidationService.updateBalanceAndCreateTransaction(
                                                                    sourceAccountId,
                                                                    newSourceBalance,
                                                                    "TRANSFER",
                                                                    amount,
                                                                    sourceAccount.getAccountNumber(),
                                                                    destinationAccount.getAccountNumber())
                                                            .then(transactionValidationService.updateBalanceAndCreateTransaction(
                                                                    destinationAccountId,
                                                                    newDestinationBalance,
                                                                    "TRANSFER",
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
        return null;
    }
}
