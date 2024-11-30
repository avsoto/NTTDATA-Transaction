package com.bankingSystem.transaction.service.util;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class TransactionUtil {

    public Mono<Void> validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("The amount must be higher than 0."));
        }
        return Mono.empty();
    }
}
