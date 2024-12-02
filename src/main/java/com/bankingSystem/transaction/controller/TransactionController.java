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


@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger LOGGER_FACTORY = LoggerFactory.getLogger(TransactionController.class);


    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Transaction>> processDeposit(
            @RequestBody @Valid DepositRequest request) {
        LOGGER_FACTORY.info("Starting deposit for account: {}", request.getAccountId());
        return transactionService.registerDeposit(request.getAccountId(), request.getAmount())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Transaction>> processWithdrawal(
            @RequestBody @Valid WithdrawalRequest request) {
        LOGGER_FACTORY.info("Starting withdrawal for account: {}", request.getAccountId());
        return transactionService.registerWithdrawal(request.getAccountId(), request.getAmount())
                .map(ResponseEntity::ok);
    }


    @PostMapping("/transferTo")
    public Mono<ResponseEntity<Transaction>> processTransfer(@RequestBody @Valid TransferRequest transferRequest) {
        LOGGER_FACTORY.info("Starting transfer from source account: {} to destination account: {}", transferRequest.getSourceAccountId(), transferRequest.getDestinationAccountId());
        return transactionService.registerTransfer(
                        transferRequest.getSourceAccountId(),
                        transferRequest.getDestinationAccountId(),
                        transferRequest.getAmount())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/history")
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionService.getTransactionHistory();
    }

}
