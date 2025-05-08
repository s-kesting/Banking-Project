package group3.bankingApp.model;

import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.enums.VerifyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 20)
    private String phoneNumber;    

    @Column(nullable = false, unique = true, length = 20)
    private String bsn;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "VERIFY_USER", nullable = false)
    private VerifyStatus verifyUser;

    // -- Constructors, getters & setters --

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBsn() {
        return bsn;
    }
    public void setBsn(String bsn) {
        this.bsn = bsn;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public VerifyStatus getVerifyUser() {
        return verifyUser;
    }
    public void setVerifyUser(VerifyStatus verifyUser) {
        this.verifyUser = verifyUser;
    }
}

