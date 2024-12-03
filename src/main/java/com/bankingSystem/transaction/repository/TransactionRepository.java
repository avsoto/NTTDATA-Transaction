package com.bankingSystem.transaction.repository;

import com.bankingSystem.transaction.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Repository interface for performing CRUD operations on {@link Transaction} objects.
 * This interface extends {@link ReactiveMongoRepository} to provide reactive MongoDB operations
 * for the {@link Transaction} entity. It supports reactive programming patterns using Project Reactor.
 */
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

}
