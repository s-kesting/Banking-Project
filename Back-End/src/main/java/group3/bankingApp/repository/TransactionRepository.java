package group3.bankingApp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM Transaction t " +
       "JOIN Account senderAcc ON t.senderAccount = senderAcc.accountId " +
       "JOIN User sender ON senderAcc.userId = sender.userId " +
       "JOIN Account receiverAcc ON t.receiverAccount = receiverAcc.accountId " +
       "JOIN User receiver ON receiverAcc.userId = receiver.userId " +
       "WHERE LOWER(sender.username) LIKE %:query% OR LOWER(receiver.username) LIKE %:query%")
    Page<Transaction> findBySenderOrReceiverUsername(@Param("query") String query, Pageable pageable);
}
