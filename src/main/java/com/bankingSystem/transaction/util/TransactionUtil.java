package com.bankingSystem.transaction.util;

import com.bankingSystem.transaction.model.Transaction;
import com.bankingSystem.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class TransactionUtil {

    public Mono<Void> validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }
        return Mono.empty();
    }
}
