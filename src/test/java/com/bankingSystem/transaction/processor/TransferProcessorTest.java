package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.exceptionhandler.InsufficientBalanceException;
import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import static org.mockito.Mockito.*;

class TransferProcessorTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionUtil transactionUtil;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private TransferProcessor transferProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    /*
    * Verifies that processing a transfer successfully updates the accounts and returns a transaction.
    * */
    void testProcessTransfer_Success() {
        // Arrange
        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        BigDecimal amount = new BigDecimal("100");

        BankAccountDTO sourceAccount = new BankAccountDTO(sourceAccountId, "123456", new BigDecimal("200"), "SAVINGS", 1);
        BankAccountDTO destinationAccount = new BankAccountDTO(destinationAccountId, "654321", new BigDecimal("300"), "SAVINGS", 2);

        when(accountServiceClient.fetchBankAccountById(sourceAccountId)).thenReturn(Mono.just(sourceAccount));
        when(accountServiceClient.fetchBankAccountById(destinationAccountId)).thenReturn(Mono.just(destinationAccount));
        when(transactionRepository.save(any())).thenReturn(Mono.just(new Transaction()));
        when(transactionUtil.validateAmount(amount)).thenReturn(Mono.empty());
        when(accountServiceClient.adjustBankAccountBalance(any(), any())).thenReturn(Mono.empty());

        // Act
        Mono<Transaction> result = transferProcessor.processTransfer(sourceAccountId, destinationAccountId, amount);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .expectComplete()
                .verify();

        verify(accountServiceClient, times(1)).fetchBankAccountById(sourceAccountId);
        verify(accountServiceClient, times(1)).fetchBankAccountById(destinationAccountId);
        verify(accountServiceClient, times(1)).adjustBankAccountBalance(eq(sourceAccountId), eq(new BigDecimal("100")));
        verify(accountServiceClient, times(1)).adjustBankAccountBalance(eq(destinationAccountId), eq(new BigDecimal("400")));
    }

    @Test
    /*
    * Verifies that processing a transfer with insufficient balance results in an InsufficientBalanceException.
    * */
    void testProcessTransfer_InsufficientBalance() {
        // Arrange
        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        BigDecimal amount = new BigDecimal("500");

        BankAccountDTO sourceAccount = new BankAccountDTO(sourceAccountId, "123456", new BigDecimal("200"), "SAVINGS", 1);
        BankAccountDTO destinationAccount = new BankAccountDTO(destinationAccountId, "654321", new BigDecimal("300"), "SAVINGS", 2);

        when(accountServiceClient.fetchBankAccountById(sourceAccountId)).thenReturn(Mono.just(sourceAccount));
        when(accountServiceClient.fetchBankAccountById(destinationAccountId)).thenReturn(Mono.just(destinationAccount));
        when(transactionUtil.validateAmount(amount)).thenReturn(Mono.empty());

        // Act
        Mono<Transaction> result = transferProcessor.processTransfer(sourceAccountId, destinationAccountId, amount);

        // Assert
        StepVerifier.create(result)
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    @Test
    /*
    *   Verifies that an invalid (negative) transfer amount results in an IllegalArgumentException.
    * */
    void testValidateTransferAmount_Failure() {
        // Arrange
        BigDecimal invalidAmount = new BigDecimal("-100");

        when(transactionUtil.validateAmount(invalidAmount)).thenReturn(Mono.error(new IllegalArgumentException("Amount must be positive")));

        // Act & Assert
        StepVerifier.create(transferProcessor.validateTransferAmount(invalidAmount))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}