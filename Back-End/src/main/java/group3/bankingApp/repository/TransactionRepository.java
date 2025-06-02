package group3.bankingApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group3.bankingApp.model.Transaction;

/**
 * TransactionRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount IN :ids OR t.receiverAccount IN :ids")
    List<Transaction> findByAccountIds(@Param("ids") List<Integer> ids);
}
