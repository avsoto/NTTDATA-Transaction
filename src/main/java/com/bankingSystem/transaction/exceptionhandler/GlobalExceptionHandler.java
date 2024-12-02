package com.bankingSystem.transaction.exceptionhandler;

import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo de excepciones generales (por ejemplo, errores no esperados)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGeneralException(Exception e) {
        logger.error("Unexpected error occurred: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later."));
    }

    // Manejo de errores de validación (por ejemplo, parámetros inválidos)
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("Invalid input: ", e);
        return Mono.just(ResponseEntity.badRequest().body("Invalid input: " + e.getMessage()));
    }

    // Manejo de excepciones de validación de Bean (errores de validación en el cuerpo de la solicitud)
    @ExceptionHandler(BindException.class)
    public Mono<ResponseEntity<String>> handleBindException(BindException e) {
        String errorMessage = e.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");
        logger.error("Validation error: ", e);
        return Mono.just(ResponseEntity.badRequest().body(errorMessage));
    }

    // Manejo de excepciones de validación de JSR-303 (por ejemplo, anotaciones @NotNull)
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Constraint violation error");
        logger.error("Constraint violation error: ", e);
        return Mono.just(ResponseEntity.badRequest().body(errorMessage));
    }

    // Manejo de errores en servicios externos (por ejemplo, errores de WebClient)
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<String>> handleWebClientResponseException(WebClientResponseException e) {
        logger.error("External service error: ", e);
        return Mono.just(ResponseEntity.status(e.getStatusCode())
                .body("External service error: " + e.getMessage()));
    }

    // Manejo de excepciones específicas de la base de datos (por ejemplo, errores de MongoDB)
    @ExceptionHandler(MongoException.class)
    public Mono<ResponseEntity<String>> handleMongoException(MongoException e) {
        logger.error("Database error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database error: " + e.getMessage()));
    }

    // Manejo de errores HTTP 4xx (errores del cliente)
    @ExceptionHandler(HttpClientErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpClientErrorException(HttpClientErrorException e) {
        logger.error("HTTP client error: ", e);
        return Mono.just(ResponseEntity.status(e.getStatusCode())
                .body("HTTP client error: " + e.getMessage()));
    }

    // Manejo de errores HTTP 5xx (errores del servidor)
    @ExceptionHandler(HttpServerErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpServerErrorException(HttpServerErrorException e) {
        logger.error("HTTP server error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("HTTP server error: " + e.getMessage()));
    }

    // Manejo de errores de entidad no encontrada (por ejemplo, cuenta no encontrada)
    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<String>> handleAccountNotFoundException(AccountNotFoundException e) {
        logger.error("Account not found: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Account not found: " + e.getMessage()));
    }

    // Manejo de errores de transacción (por ejemplo, saldo insuficiente)
    @ExceptionHandler(TransactionException.class)
    public Mono<ResponseEntity<String>> handleTransactionException(TransactionException e) {
        logger.error("Transaction error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Transaction error: " + e.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ResponseEntity<String>> handleInsufficientBalanceException(InsufficientBalanceException e) {
        logger.error("Insufficient balance: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage()));
    }

}
