package group3.bankingApp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ATMSESSION")
public class ATMSession {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionId;

    @Column(nullable = false)
    private Integer userId; // FK to users.userId

    @Column(name = "LOGIN_TIME", nullable = false)
    private LocalDateTime loginTime;

    // -- Getters & Setters --

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}
