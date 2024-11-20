package com.bankingSystem.transaction.repository;

import com.bankingSystem.transaction.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

    Flux<Transaction> findByOriginAccount(String originAccount);
    Flux<Transaction> findByDestinationAccount(String destinationAccount);
}
