package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.model.dto.BankAccountDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WithdrawalProcessorTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionUtil transactionUtil;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private WithdrawalProcessor withdrawalProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    /*
    * Verifies that processing a successful withdrawal transaction updates the account balance and returns the correct transaction.
    * */
    void processTransaction_SuccessfulWithdrawal_ReturnsTransaction() {
        // Arrange
        Integer accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(100.00);

        BankAccountDTO account = new BankAccountDTO();
        account.setBalance(BigDecimal.valueOf(200.00));

        // Mock the validation and account fetch
        when(transactionUtil.validateAmount(amount)).thenReturn(Mono.empty());
        when(accountServiceClient.fetchBankAccountById(accountId)).thenReturn(Mono.just(account));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setAmount(amount);
        mockTransaction.setType(TransactionType.WITHDRAWAL);
        mockTransaction.setOriginAccount("1");

        when(accountServiceClient.adjustBankAccountBalance(accountId, account.getBalance().subtract(amount)))
                .thenReturn(Mono.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(mockTransaction));

        // Act
        Mono<Transaction> result = withdrawalProcessor.processTransaction(accountId, amount);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(transaction -> {
                    assertEquals(amount, transaction.getAmount());
                    assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
                    assertEquals("1", transaction.getOriginAccount());
                    return true;
                })
                .expectComplete()
                .verify();

        verify(accountServiceClient, times(1)).fetchBankAccountById(accountId);
        verify(accountServiceClient, times(1)).adjustBankAccountBalance(accountId, account.getBalance().subtract(amount));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    /*
    * Verifies that processing a withdrawal with insufficient balance returns an error Mono with an IllegalArgumentException.
     * */
    void processTransaction_InsufficientBalance_ReturnsErrorMono() {
        // Arrange
        Integer accountId = 1;
        BigDecimal amount = BigDecimal.valueOf(500.00);

        BankAccountDTO account = new BankAccountDTO();
        account.setBalance(BigDecimal.valueOf(100.00));

        when(transactionUtil.validateAmount(amount)).thenReturn(Mono.empty());
        when(accountServiceClient.fetchBankAccountById(accountId)).thenReturn(Mono.just(account));

        // Act
        Mono<Transaction> result = withdrawalProcessor.processTransaction(accountId, amount);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(accountServiceClient, times(1)).fetchBankAccountById(accountId);
        verify(accountServiceClient, times(0)).adjustBankAccountBalance(any(), any());
        verify(transactionRepository, times(0)).save(any());
    }
}
