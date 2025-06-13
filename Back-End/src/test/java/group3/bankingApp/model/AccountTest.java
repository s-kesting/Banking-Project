package group3.bankingApp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group3.bankingApp.model.enums.AccountType;
import group3.bankingApp.model.enums.Role;
import group3.bankingApp.model.enums.VerifyStatus;

public class AccountTest {

    private Account account;

    @BeforeEach
    void init() {
        account = new Account();
    }

    @Test
    void newaccountShouldNotBeNull() {
        assertNotNull(account, "A newly created User should not be null");
    }

    @Test
    void gettersAndSettersShouldWork() {
        account.setUserId(123);
        assertEquals(123, account.getUserId());

        account.setAccountId(123);
        assertEquals(123, account.getAccountId());

        account.setIBAN("IBAN");
        assertEquals("IBAN", account.getIBAN());

        account.setBalance(123);
        assertEquals(123, account.getBalance());

        account.setDailyLimit(123);
        assertEquals(123, account.getDailyLimit());

        account.setAbsoluteLimit(123);
        assertEquals(123, account.getAbsoluteLimit());

        account.setVerifyAccount(VerifyStatus.ACTIVE);
        assertEquals(VerifyStatus.ACTIVE, account.getVerifyAccount());

        account.setAccountType(AccountType.Checking);
        assertEquals(AccountType.Checking, account.getAccountType());
    }
}
