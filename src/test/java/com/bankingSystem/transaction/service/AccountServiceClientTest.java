package com.bankingSystem.transaction.service;

import com.bankingSystem.transaction.exceptionhandler.AccountServiceErrorHandler;
import com.bankingSystem.transaction.model.dto.BankAccountDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceClientTest {

    @Mock
    private AccountServiceErrorHandler errorHandler;

    @InjectMocks
    private AccountServiceClient accountServiceClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        accountServiceClient = new AccountServiceClient(webClient, errorHandler, mockWebServer.url("/").toString());
    }

    @Test
    /*
    * Verifies that fetching a bank account by ID returns the correct account details.
    * */
    void fetchBankAccountById_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ \"id\": 123, \"accountNumber\": \"987654321\", \"balance\": 1000.00, \"accountType\": \"SAVINGS\", \"customerId\": 456 }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;

        Mono<BankAccountDTO> result = accountServiceClient.fetchBankAccountById(accountId);

        BankAccountDTO bankAccountDTO = result.block();
        assertNotNull(bankAccountDTO);
        assertEquals(accountId, bankAccountDTO.getId());
        assertEquals("987654321", bankAccountDTO.getAccountNumber());
        assertEquals(1000.00, bankAccountDTO.getBalance().doubleValue());
        assertEquals("SAVINGS", bankAccountDTO.getAccountType());
        assertEquals(456, bankAccountDTO.getCustomerId());
    }

    @Test
    /*
    *   Verifies that adjusting the bank account balance successfully completes without errors.
    * */
    void adjustBankAccountBalance_Success() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;
        BigDecimal newBalance = BigDecimal.valueOf(1500.00);

        Mono<Void> result = accountServiceClient.adjustBankAccountBalance(accountId, newBalance);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    /*
    * Verifies that an error response during bank account balance adjustment throws an exception.
    * */
    void adjustBankAccountBalance_Error() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{ \"error\": \"Internal Server Error\" }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;
        BigDecimal newBalance = BigDecimal.valueOf(1500.00);

        Mono<Void> result = accountServiceClient.adjustBankAccountBalance(accountId, newBalance);

        assertThrows(Exception.class, result::block);
    }

    @AfterEach
    /*
    *  Ensures the mock server is shut down after each test to clean up resources.
    * */
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }


}