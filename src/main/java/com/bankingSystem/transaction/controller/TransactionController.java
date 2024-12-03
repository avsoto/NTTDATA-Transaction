package com.bankingSystem.transaction.controller;
import com.bankingSystem.transaction.model.request.DepositRequest;
import com.bankingSystem.transaction.model.request.TransferRequest;
import com.bankingSystem.transaction.model.request.WithdrawalRequest;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for handling transaction-related operations.
 * This controller provides endpoints for deposit, withdrawal, transfer,
 * and fetching transaction history. It uses reactive programming with {@link Mono}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger LOGGER_FACTORY = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    /**
     * Processes a deposit request.
     * @param request the {@link DepositRequest} containing account ID and deposit amount
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the created {@link Transaction}
     */
    @PostMapping("/deposit")
    public Mono<ResponseEntity<Transaction>> processDeposit(
            @RequestBody @Valid DepositRequest request) {
        LOGGER_FACTORY.info("Starting deposit for account: {}", request.getAccountId());
        return transactionService.registerDeposit(request.getAccountId(), request.getAmount())
                .map(ResponseEntity::ok);
    }

    /**
     * Processes a withdrawal request.
     * @param request the {@link WithdrawalRequest} containing account ID and withdrawal amount
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the created {@link Transaction}
     */
    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Transaction>> processWithdrawal(
            @RequestBody @Valid WithdrawalRequest request) {
        LOGGER_FACTORY.info("Starting withdrawal for account: {}", request.getAccountId());
        return transactionService.registerWithdrawal(request.getAccountId(), request.getAmount())
                .map(ResponseEntity::ok);
    }

    /**
     * Processes a transfer request between two accounts.
     * @param transferRequest the {@link TransferRequest} containing source account ID, destination account ID, and transfer amount
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the created {@link Transaction}
     */
    @PostMapping("/transferTo")
    public Mono<ResponseEntity<Transaction>> processTransfer(@RequestBody @Valid TransferRequest transferRequest) {
        LOGGER_FACTORY.info("Starting transfer from source account: {} to destination account: {}", transferRequest.getSourceAccountId(), transferRequest.getDestinationAccountId());
        return transactionService.registerTransfer(
                        transferRequest.getSourceAccountId(),
                        transferRequest.getDestinationAccountId(),
                        transferRequest.getAmount())
                .map(ResponseEntity::ok);
    }

    /**
     * Retrieves the transaction history for the current user.
     * @return a {@link Mono} emitting a list of {@link Transaction} objects
     */
    @GetMapping("/history")
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionService.getTransactionHistory();
    }
}
