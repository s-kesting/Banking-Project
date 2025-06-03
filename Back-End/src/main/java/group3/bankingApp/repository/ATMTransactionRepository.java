package group3.bankingApp.repository;

import group3.bankingApp.model.ATMTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ATMTransactionRepository extends JpaRepository<ATMTransaction, Integer> {
}
