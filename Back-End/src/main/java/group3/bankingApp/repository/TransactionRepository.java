package group3.bankingApp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.model.Transaction;

/**
 * TransactionRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount IN :ids OR t.receiverAccount IN :ids")
    List<Transaction> findByAccountIds(@Param("ids") List<Integer> ids);

    ////////////Robben - Get List of Transaction/////////////////
        @Query("SELECT new group3.bankingApp.DTO.TransactionJoinDTO(" +
        "t.transactionId, senderOwner.username, senderAcc.IBAN, receiverAcc.IBAN, receiverOwner.username, " +
        "t.amount, t.description, t.createdAt) " +
        "FROM Transaction t " +
        "JOIN Account senderAcc ON t.senderAccount = senderAcc.accountId " +
        "JOIN User senderOwner ON senderAcc.userId = senderOwner.userId " +
        "JOIN Account receiverAcc ON t.receiverAccount = receiverAcc.accountId " +
        "JOIN User receiverOwner ON receiverAcc.userId = receiverOwner.userId")
        Page<TransactionJoinDTO> findAllJoinDTO(Pageable pageable);

        @Query("SELECT new group3.bankingApp.DTO.TransactionJoinDTO(" +
        "t.transactionId, senderOwner.username, senderAcc.IBAN, receiverAcc.IBAN, receiverOwner.username, " +
        "t.amount, t.description, t.createdAt) " +
        "FROM Transaction t " +
        "JOIN Account senderAcc ON t.senderAccount = senderAcc.accountId " +
        "JOIN User senderOwner ON senderAcc.userId = senderOwner.userId " +
        "JOIN Account receiverAcc ON t.receiverAccount = receiverAcc.accountId " +
        "JOIN User receiverOwner ON receiverAcc.userId = receiverOwner.userId " +
        "WHERE LOWER(senderOwner.username) LIKE %:query% OR LOWER(receiverOwner.username) LIKE %:query%")
        Page<TransactionJoinDTO> findBySenderOrReceiverUsernameJoinDTO(@Param("query") String query, Pageable pageable);


    List<Transaction> findBySender_UserIdOrReceiver_UserId(int senderUserId, int recieverUserId);

    Page<TransactionJoinDTO> findBySender_IBANOrReceiver_IBAN(String senderIban, String receiverIban,
            Pageable pageable);

    @Query("""
      SELECT DISTINCT
        CASE 
          WHEN t.sender.owner.userId = :userId 
            THEN t.receiver.IBAN 
          ELSE t.sender.IBAN 
        END
      FROM Transaction t
      WHERE (t.sender.owner.userId = :userId
             OR t.receiver.owner.userId = :userId)
        AND (LOWER(t.sender.owner.username)   LIKE %:name%
           OR LOWER(t.receiver.owner.username) LIKE %:name%)""")
    List<String> findCounterpartyIbansByName(@Param("userId") Integer userId, @Param("name")   String name);
}
