package group3.bankingApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import group3.bankingApp.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // @Query("SELECT a.ACCOUNTID, a.USERID, a.IBAN, a.BALANCE, a.DAILY_LIMIT,
    // a.ABSOLUTE_LIMIT, a.VERIFY_ACCOUNT, a.ACCOUNT_TYPE FROM ACCOUNT AS a WHERE
    // USERID = :userId")
    Optional<Account> findByUserId(int userId);
}
