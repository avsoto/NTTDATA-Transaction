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

/**
 * Global exception handler for managing exceptions throughout the application.
 * This class provides centralized exception handling using the {@link ControllerAdvice} annotation.
 * It captures various exceptions and maps them to appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles general exceptions (e.g., unexpected errors).
     * @return a {@link Mono} emitting a {@link ResponseEntity} with an internal server error status
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGeneralException(Exception e) {
        logger.error("Unexpected error occurred: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later."));
    }

    /**
     * Handles {@link IllegalArgumentException}, typically caused by invalid input parameters.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with a bad request status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("Invalid input: ", e);
        return Mono.just(ResponseEntity.badRequest().body("Invalid input: " + e.getMessage()));
    }

    /**
     * Handles {@link BindException} for validation errors in request bodies.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with a bad request status
     */
    @ExceptionHandler(BindException.class)
    public Mono<ResponseEntity<String>> handleBindException(BindException e) {
        String errorMessage = e.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");
        logger.error("Validation error: ", e);
        return Mono.just(ResponseEntity.badRequest().body(errorMessage));
    }

    /**
     * Handles {@link ConstraintViolationException} for JSR-303 validation errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with a bad request status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Constraint violation error");
        logger.error("Constraint violation error: ", e);
        return Mono.just(ResponseEntity.badRequest().body(errorMessage));
    }

    /**
     * Handles {@link WebClientResponseException} for errors from external services.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with the status from the external service
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<String>> handleWebClientResponseException(WebClientResponseException e) {
        logger.error("External service error: ", e);
        return Mono.just(ResponseEntity.status(e.getStatusCode())
                .body("External service error: " + e.getMessage()));
    }

    /**
     * Handles {@link MongoException} for database-related errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with an internal server error status
     */
    @ExceptionHandler(MongoException.class)
    public Mono<ResponseEntity<String>> handleMongoException(MongoException e) {
        logger.error("Database error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database error: " + e.getMessage()));
    }

    /**
     * Handles {@link HttpClientErrorException} for 4xx HTTP errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with the client error status
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpClientErrorException(HttpClientErrorException e) {
        logger.error("HTTP client error: ", e);
        return Mono.just(ResponseEntity.status(e.getStatusCode())
                .body("HTTP client error: " + e.getMessage()));
    }

    /**
     * Handles {@link HttpServerErrorException} for 5xx HTTP errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with an internal server error status
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public Mono<ResponseEntity<String>> handleHttpServerErrorException(HttpServerErrorException e) {
        logger.error("HTTP server error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("HTTP server error: " + e.getMessage()));
    }

    /**
     * Handles {@link AccountNotFoundException} for account-related errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with a not found status
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<String>> handleAccountNotFoundException(AccountNotFoundException e) {
        logger.error("Account not found: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Account not found: " + e.getMessage()));
    }

    /**
     * Handles {@link TransactionException} for transaction-related errors.
     * @return a {@link Mono} emitting a {@link ResponseEntity} with a bad request status
     */
    @ExceptionHandler(TransactionException.class)
    public Mono<ResponseEntity<String>> handleTransactionException(TransactionException e) {
        logger.error("Transaction error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Transaction error: " + e.getMessage()));
    }

}
