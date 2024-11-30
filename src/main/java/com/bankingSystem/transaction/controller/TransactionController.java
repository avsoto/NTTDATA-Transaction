package com.bankingSystem.transaction.controller;
import com.bankingSystem.transaction.dto.TransferRequest;
import com.bankingSystem.transaction.dto.WithdrawalRequest;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Transaction>> registerDeposit(
            @RequestParam Integer accountId,
            @RequestParam BigDecimal amount) {
        logger.info("Starting deposit for account: {}", accountId);
        return transactionService.registerDeposit(accountId, amount)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Transaction>> registerWithdraw(
            @RequestParam Integer accountId,
            @RequestBody WithdrawalRequest request) {
        logger.info("Starting withdrawal for account: {}", accountId);
        return transactionService.registerWithdrawal(accountId, request.getAmount())
                .map(ResponseEntity::ok);
    }


    @PostMapping("/transferTo")
    public Mono<ResponseEntity<Transaction>> transferMoney(@RequestBody TransferRequest transferRequest) {
        logger.info("Starting transfer from source account: {} to destination account: {}", transferRequest.getSourceAccountId(), transferRequest.getDestinationAccountId());
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
