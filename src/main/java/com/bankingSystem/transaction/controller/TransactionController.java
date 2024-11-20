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


@RestController
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

    @PostMapping("/withdraw")
    public Mono<ResponseEntity<Transaction>> registerWithdraw(
            @RequestParam("accountId") Integer accountId,
            @RequestBody WithdrawalRequest request) {
        return transactionService.registerWithdrawal(accountId, request.getAmount())
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().body(null)));
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<Transaction>> transferMoney(@RequestBody TransferRequest transferRequest) {
        return transactionService.registerTransfer(
                        transferRequest.getSourceAccountId(),
                        transferRequest.getDestinationAccountId(),
                        transferRequest.getAmount()
                ).map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }


}
