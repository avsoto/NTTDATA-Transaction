package com.bankingSystem.transaction.controller;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.model.TransactionType;
import com.bankingSystem.transaction.model.request.DepositRequest;
import com.bankingSystem.transaction.model.request.TransferRequest;
import com.bankingSystem.transaction.model.request.WithdrawalRequest;
import com.bankingSystem.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.http.HttpStatus.OK;

@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction.TransactionBuilder transactionBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    /*
    * Verifies that processing a deposit request returns the correct transaction with a 200 OK status.
     * */
    void processDeposit_shouldReturnTransaction() {
        // Arrange
        DepositRequest depositRequest = new DepositRequest(1, BigDecimal.valueOf(1245.12));
        Transaction mockTransaction = Transaction.builder()
                .type(TransactionType.SAVING)
                .amount(new BigDecimal("100"))
                .date(LocalDateTime.now())
                .originAccount("12345")
                .destinationAccount("12345")
                .build();

        when(transactionService.registerDeposit(depositRequest.getAccountId(), depositRequest.getAmount()))
                .thenReturn(Mono.just(mockTransaction));

        // Act and Assert
        webTestClient.post()
                .uri("/transaction/deposit")
                .bodyValue(depositRequest)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(Transaction.class)
                .isEqualTo(mockTransaction);
    }


    @Test
    /*
    *  Verifies that processing a withdrawal request returns the correct transaction with a 200 OK status.
    * */
    void processWithdrawal_shouldReturnTransaction() {
        // Arrange
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(1, new BigDecimal("50"));
        Transaction mockTransaction = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("50"))
                .date(LocalDateTime.now())
                .originAccount("12345")
                .destinationAccount("12345")
                .build();

        when(transactionService.registerWithdrawal(withdrawalRequest.getAccountId(), withdrawalRequest.getAmount()))
                .thenReturn(Mono.just(mockTransaction));

        // Act and Assert
        webTestClient.post()
                .uri("/transaction/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withdrawalRequest)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(Transaction.class)
                .isEqualTo(mockTransaction);
    }

    @Test
    /*
    * Verifies that processing a transfer request returns the correct transaction with a 200 OK status.
    * */
    void processTransfer_shouldReturnTransaction() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest(1, 2, new BigDecimal("50"));
        Transaction mockTransaction = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(new BigDecimal("50"))
                .date(LocalDateTime.now())
                .originAccount("12345")
                .destinationAccount("67890")
                .build();

        when(transactionService.registerTransfer(transferRequest.getSourceAccountId(), transferRequest.getDestinationAccountId(), transferRequest.getAmount()))
                .thenReturn(Mono.just(mockTransaction));

        // Act and Assert
        webTestClient.post()
                .uri("/transaction/transferTo")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(Transaction.class)
                .isEqualTo(mockTransaction);
    }

    @Test
    /*
    * Verifies that fetching the transaction history returns a list of transactions with a 200 OK status.
    * */
    void getTransactionHistory_shouldReturnListOfTransactions() {
        // Arrange
        Transaction mockTransaction = Transaction.builder()
                .type(TransactionType.SAVING)
                .amount(new BigDecimal("100"))
                .date(LocalDateTime.now())
                .originAccount("12345")
                .destinationAccount("12345")
                .build();

        when(transactionService.getTransactionHistory()).thenReturn(Mono.just(List.of(mockTransaction)));

        // Act and Assert
        webTestClient.get()
                .uri("/transaction/history")
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBodyList(Transaction.class)
                .contains(mockTransaction);
    }
}