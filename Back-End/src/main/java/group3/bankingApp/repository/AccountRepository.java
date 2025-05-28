package group3.bankingApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import group3.bankingApp.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // @Query("SELECT a.ACCOUNTID, a.USERID, a.IBAN, a.BALANCE, a.DAILY_LIMIT,
    // a.ABSOLUTE_LIMIT, a.VERIFY_ACCOUNT, a.ACCOUNT_TYPE FROM ACCOUNT AS a WHERE
    // USERID = :userId")

    @Modifying
    @Query("Update Account a SET a.balance = a.balance - :amount WHERE a.accountId =:id")
    void withdraw(@Param("id") Integer id, @Param("amount") double amount);

    @Modifying
    @Query("Update Account a SET a.balance = a.balance + :amount WHERE a.accountId =:id")
    void deposit(@Param("id") Integer id, @Param("amount") double amount);

    List<Account> findByUserId(int userId);

}
