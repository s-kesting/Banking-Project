package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group3.bankingApp.model.Account;
import group3.bankingApp.model.Transaction;
import java.util.Optional;

/**
 * TransactionRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}
