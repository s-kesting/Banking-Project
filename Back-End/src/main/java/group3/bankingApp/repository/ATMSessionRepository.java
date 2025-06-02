package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import group3.bankingApp.model.ATMSession;

public interface ATMSessionRepository extends JpaRepository<ATMSession, Integer> {
}
