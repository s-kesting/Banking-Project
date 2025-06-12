package group3.bankingApp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group3.bankingApp.DTO.TransactionDTO;
import group3.bankingApp.DTO.TransactionJoinDTO;
import group3.bankingApp.model.Transaction;

/**
 * TransactionRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount IN :ids OR t.receiverAccount IN :ids")
    List<Transaction> findByAccountIds(@Param("ids") List<Integer> ids);

    //////////// Robben - Get List of Transaction/////////////////
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

    @Query("SELECT new group3.bankingApp.DTO.TransactionJoinDTO(" +
            "t1_0.transactionId, o1_0.username, s1_0.IBAN, r1_0.IBAN, o2_0.username, " +
            "t1_0.amount, t1_0.description, t1_0.createdAt) " +
            "from Transaction t1_0 " +
            "left join Account s1_0 on s1_0.accountId=t1_0.senderAccount " +
            "left join User o1_0 on o1_0.userId=s1_0.userId " +
            "left join Account r1_0 on r1_0.accountId=t1_0.receiverAccount " +
            "left join User o2_0 on o2_0.userId=r1_0.userId " +
            "where  (r1_0.IBAN=:receiverIban or s1_0.IBAN=:senderIban) " +
            "AND (:startDate IS NULL OR t1_0.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t1_0.createdAt <= :endDate) " +
            "AND (:minAmount IS NULL OR t1_0.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR t1_0.amount <= :maxAmount) " +
            "AND (:exactAmount IS NULL OR t1_0.amount = :exactAmount) " +
            "AND (:filterIban IS NULL OR s1_0.IBAN = :filterIban OR r1_0.IBAN = :filterIban)")

    Page<TransactionJoinDTO> findBySender_IBANOrReceiver_IBANWithFilter(@Param("senderIban") String senderIban,
            @Param("receiverIban") String receiverIban,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") String minAmount,
            @Param("maxAmount") String maxAmount,
            @Param("exactAmount") String exactAmount,
            @Param("filterIban") String filterIban, Pageable pageable);

}
