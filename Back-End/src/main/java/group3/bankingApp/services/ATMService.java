package group3.bankingApp.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import group3.bankingApp.repository.ATMRepository;

@Service
public class ATMService {
    private final ATMRepository atmRepository;

    public ATMService(ATMRepository atmRepository) {
        this.atmRepository = atmRepository;
    }
}
