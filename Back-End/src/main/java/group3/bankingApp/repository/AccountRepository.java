package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group3.bankingApp.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

}
