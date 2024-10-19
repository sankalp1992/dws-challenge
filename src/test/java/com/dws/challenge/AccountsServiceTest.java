package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @MockBean
  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Clear all accounts before each test
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void transferMoney_successfulTransfer() {
    // Create accounts
    Account accountFrom = new Account("Id-1");
    accountFrom.setBalance(new BigDecimal("1000"));

    Account accountTo = new Account("Id-2");
    accountTo.setBalance(new BigDecimal("500"));

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    BigDecimal transferAmount = new BigDecimal("200");

    // call to transferMoney
    this.accountsService.transferMoney("Id-1", "Id-2", transferAmount);

    // Assert
    assertThat(this.accountsService.getAccount("Id-1").getBalance()).isEqualTo(new BigDecimal("800"));
    assertThat(this.accountsService.getAccount("Id-2").getBalance()).isEqualTo(new BigDecimal("700"));

    // Verify that notifications are sent
    verify(notificationService).notifyAboutTransfer(accountFrom, "Transferred 200 to account Id-2");
    verify(notificationService).notifyAboutTransfer(accountTo, "Received 200 from account Id-1");
  }

  @Test
  void transferMoney_failsWhenInsufficientBalance() {
    // Create accounts
    Account accountFrom = new Account("Id-1");
    accountFrom.setBalance(new BigDecimal("100"));

    Account accountTo = new Account("Id-2");
    accountTo.setBalance(new BigDecimal("500"));

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    BigDecimal transferAmount = new BigDecimal("200");

    // Act & Assert
    try {
      this.accountsService.transferMoney("Id-1", "Id-2", transferAmount);
      fail("Should have failed due to insufficient balance");
    } catch (InsufficientBalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Insufficient balance in account: Id-1");
    }

    // Verify that notifications are NOT sent
    verify(notificationService, never()).notifyAboutTransfer(any(), anyString());
  }


}
