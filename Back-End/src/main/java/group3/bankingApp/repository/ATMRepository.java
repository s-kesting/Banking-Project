package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group3.bankingApp.repository.AccountRepository;
import group3.bankingApp.model.ATMSession;

/**
 * ATMRepository
 */
public interface ATMRepository extends JpaRepository<ATMSession, Integer> {

}
