package com.bankingSystem.transaction.factory;

import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.processor.DepositProcessor;
import com.bankingSystem.transaction.processor.TransactionProcessor;
import com.bankingSystem.transaction.processor.TransferProcessor;
import com.bankingSystem.transaction.processor.WithdrawalProcessor;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class TransactionProcessorFactoryTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionUtil transactionUtil;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private TransactionProcessorFactory transactionProcessorFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("buildTransactionProcessor_SAVING_ReturnsDepositProcessor")
    void buildTransactionProcessor_Deposit_ReturnsDepositProcessor() {
        // Arrange
        TransactionType type = TransactionType.SAVING;

        // Act
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(type);

        // Assert
        assertTrue(processor instanceof DepositProcessor, "Expected DepositProcessor for SAVING type");
    }

    @Test
    @DisplayName("buildTransactionProcessor_WITHDRAWAL_ReturnsWithdrawalProcessor")
    void buildTransactionProcessor_Withdrawal_ReturnsWithdrawalProcessor() {
        // Arrange
        TransactionType type = TransactionType.WITHDRAWAL;

        // Act
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(type);

        // Assert
        assertTrue(processor instanceof WithdrawalProcessor, "Expected WithdrawalProcessor for WITHDRAWAL type");
    }

    @Test
    @DisplayName("buildTransactionProcessor_TRANSFER_ReturnsTransferProcessor")
    void buildTransactionProcessor_Transfer_ReturnsTransferProcessor() {
        // Arrange
        TransactionType type = TransactionType.TRANSFER;

        // Act
        TransactionProcessor processor = transactionProcessorFactory.buildTransactionProcessor(type);

        // Assert
        assertTrue(processor instanceof TransferProcessor, "Expected TransferProcessor for TRANSFER type");
    }
}
