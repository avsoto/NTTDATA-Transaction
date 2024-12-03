package com.bankingSystem.transaction.service.impl;

import com.bankingSystem.transaction.factory.TransactionProcessorFactory;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.processor.TransactionProcessor;
import com.bankingSystem.transaction.processor.TransferProcessor;
import com.bankingSystem.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionProcessorFactory transactionProcessorFactory;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProcessor transactionProcessor;

    @Mock
    private TransferProcessor transferProcessor;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(1245.12))
                .type(TransactionType.SAVING)
                .build();
    }

    @Test
    /*
    * Verifies that a valid deposit request returns the expected transaction after processing.
    * */
    void registerDeposit_ValidRequest_ReturnsTransaction() {
        // Arrange
        when(transactionProcessorFactory.buildTransactionProcessor(TransactionType.SAVING))
                .thenReturn(transactionProcessor);
        when(transactionProcessor.processTransaction(1, BigDecimal.valueOf(1245.12)))
                .thenReturn(Mono.just(transaction));

        // Act
        Mono<Transaction> response = transactionService.registerDeposit(1, BigDecimal.valueOf(1245.12));

        // Assert
        StepVerifier.create(response)
                .expectNext(transaction)
                .expectComplete()
                .verify();

        verify(transactionProcessorFactory, times(1)).buildTransactionProcessor(TransactionType.SAVING);
        verify(transactionProcessor, times(1)).processTransaction(1, BigDecimal.valueOf(1245.12));
    }

    @Test
    /*
    *   Verifies that a valid withdrawal request returns the expected transaction after processing.
    * */
    void registerWithdrawal_ValidRequest_ReturnsTransaction() {
        // Arrange
        when(transactionProcessorFactory.buildTransactionProcessor(TransactionType.WITHDRAWAL))
                .thenReturn(transactionProcessor);
        when(transactionProcessor.processTransaction(1, BigDecimal.valueOf(500.0)))
                .thenReturn(Mono.just(transaction));

        // Act
        Mono<Transaction> response = transactionService.registerWithdrawal(1, BigDecimal.valueOf(500.0));

        // Assert
        StepVerifier.create(response)
                .expectNext(transaction)
                .expectComplete()
                .verify();

        verify(transactionProcessorFactory, times(1)).buildTransactionProcessor(TransactionType.WITHDRAWAL);
        verify(transactionProcessor, times(1)).processTransaction(1, BigDecimal.valueOf(500.0));
    }

    @Test
    /*
    *   Verifies that a valid transfer request returns the expected transaction after processing.
    * */
    void registerTransfer_ValidRequest_ReturnsTransaction() {
        // Arrange
        when(transactionProcessorFactory.buildTransactionProcessor(TransactionType.TRANSFER))
                .thenReturn(transferProcessor);
        when(transferProcessor.processTransfer(1, 2, BigDecimal.valueOf(1000.0)))
                .thenReturn(Mono.just(transaction));

        // Act
        Mono<Transaction> response = transactionService.registerTransfer(1, 2, BigDecimal.valueOf(1000.0));

        // Assert
        StepVerifier.create(response)
                .expectNext(transaction)
                .expectComplete()
                .verify();

        verify(transactionProcessorFactory, times(1)).buildTransactionProcessor(TransactionType.TRANSFER);
        verify(transferProcessor, times(1)).processTransfer(1, 2, BigDecimal.valueOf(1000.0));
    }

    @Test
    /*
    * Verifies that retrieving the transaction history returns the expected list of transactions.
    * */
    void getTransactionHistory_ReturnsListOfTransactions() {
        // Arrange
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findAll()).thenReturn(Flux.fromIterable(transactions));

        // Act
        Mono<List<Transaction>> response = transactionService.getTransactionHistory();

        // Assert
        StepVerifier.create(response)
                .expectNext(transactions)
                .expectComplete()
                .verify();

        verify(transactionRepository, times(1)).findAll();
    }
}
