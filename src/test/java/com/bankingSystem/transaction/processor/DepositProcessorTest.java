package com.bankingSystem.transaction.processor;

import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import com.bankingSystem.transaction.repository.TransactionRepository;
import com.bankingSystem.transaction.service.AccountServiceClient;
import com.bankingSystem.transaction.util.TransactionUtil;

import org.mockito.*;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepositProcessorTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransactionUtil transactionUtil;

    @Mock
    private DepositProcessor depositProcessor;

    private Integer accountId;
    private BigDecimal depositAmount;

    private BankAccountDTO bankAccountDTO;

}
