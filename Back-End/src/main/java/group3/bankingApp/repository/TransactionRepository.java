package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group3.bankingApp.model.Transaction;

/**
 * TransactionRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}
