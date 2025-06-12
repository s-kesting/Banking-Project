package group3.bankingApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group3.bankingApp.model.Account;
import org.springframework.data.domain.Pageable;
import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.VerifyStatus;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Modifying
    @Query("Update Account a SET a.balance = a.balance - :amount WHERE a.accountId =:id")
    void withdraw(@Param("id") Integer id, @Param("amount") double amount);

    @Modifying
    @Query("Update Account a SET a.balance = a.balance + :amount WHERE a.accountId =:id")
    void deposit(@Param("id") Integer id, @Param("amount") double amount);

    List<Account> findByUserId(int userId);

    Page<Account> findByUserId(int userId, Pageable pageable);

    Optional<Account> findByIBAN(String IBAN);

    boolean existsByUserIdAndVerifyAccount(int userId, VerifyStatus verifyAccount);

    boolean existsByUserIdAndIBAN(int userId, String IBAN);

    List<Account> findByUserIdAndAccountType(int userId, AccountType accountType);

    Page<Account> findByUserIdAndAccountTypeAndVerifyAccount(int userId, AccountType accountType,
            VerifyStatus verifyAccount, Pageable pageable);

    Page<Account> findByUserIdAndVerifyAccount(int userId, VerifyStatus verifyAccount, Pageable pageable);

    // Check whether bank number is exist
    boolean existsByIBAN(String IBAN);

}
