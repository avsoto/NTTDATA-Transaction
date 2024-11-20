package com.bankingSystem.transaction.controller;
import com.bankingSystem.transaction.dto.TransferRequest;
import com.bankingSystem.transaction.dto.WithdrawalRequest;
import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
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

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Transaction>> registerDeposit(
            @RequestParam Integer accountId,
            @RequestParam BigDecimal amount) {
        return transactionService.registerDeposit(accountId, amount)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(null)));
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Transaction>> registerWithdraw(
            @RequestParam("accountId") Integer accountId,
            @RequestBody WithdrawalRequest request) {
        return transactionService.registerWithdrawal(accountId, request.getAmount())
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    System.err.println("Error during withdrawal: " + error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                });
    }

    @PostMapping("/transferTo")
    public Mono<ResponseEntity<Transaction>> transferMoney(@RequestBody TransferRequest transferRequest) {
        return transactionService.registerTransfer(
                        transferRequest.getSourceAccountId(),
                        transferRequest.getDestinationAccountId(),
                        transferRequest.getAmount()
                ).map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    System.err.println("Error during transfer: " + error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                });    }

    @GetMapping("/history")
    public Mono<List<Transaction>> getTransactionHistory() {
        return transactionService.getTransactionHistory();
    }


}
