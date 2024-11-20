package com.bankingSystem.transaction.repository;

import com.bankingSystem.transaction.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
}
