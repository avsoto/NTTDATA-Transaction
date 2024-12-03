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
        // Crear el servidor simulado
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Configurar WebClient para que apunte al MockWebServer
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        // Injectar WebClient en el servicio
        accountServiceClient = new AccountServiceClient(webClient, errorHandler, mockWebServer.url("/").toString());
    }

    @Test
    void fetchBankAccountById_Success() {
        // Respuesta simulada con datos de BankAccountDTO
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ \"id\": 123, \"accountNumber\": \"987654321\", \"balance\": 1000.00, \"accountType\": \"SAVINGS\", \"customerId\": 456 }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;

        // Actuar
        Mono<BankAccountDTO> result = accountServiceClient.fetchBankAccountById(accountId);

        // Afirmar
        BankAccountDTO bankAccountDTO = result.block();
        assertNotNull(bankAccountDTO);
        assertEquals(accountId, bankAccountDTO.getId());
        assertEquals("987654321", bankAccountDTO.getAccountNumber());
        assertEquals(1000.00, bankAccountDTO.getBalance().doubleValue());
        assertEquals("SAVINGS", bankAccountDTO.getAccountType());
        assertEquals(456, bankAccountDTO.getCustomerId());
    }

    @Test
    void adjustBankAccountBalance_Success() {
        // Respuesta exitosa de ajuste de saldo
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;
        BigDecimal newBalance = BigDecimal.valueOf(1500.00);

        // Actuar
        Mono<Void> result = accountServiceClient.adjustBankAccountBalance(accountId, newBalance);

        // Afirmar
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void adjustBankAccountBalance_Error() {
        // Respuesta de error para ajuste de saldo
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{ \"error\": \"Internal Server Error\" }")
                .addHeader("Content-Type", "application/json"));

        Integer accountId = 123;
        BigDecimal newBalance = BigDecimal.valueOf(1500.00);

        // Actuar
        Mono<Void> result = accountServiceClient.adjustBankAccountBalance(accountId, newBalance);

        // Afirmar
        assertThrows(Exception.class, result::block);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Cerrar el servidor simulado después de cada prueba
        mockWebServer.shutdown();
    }


}