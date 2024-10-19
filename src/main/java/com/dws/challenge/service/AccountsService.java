package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {
  @Getter
  private final AccountsRepository accountsRepository;
  private final NotificationService notificationService;
  private final Lock lock = new ReentrantLock();  // for thread safety

  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
    // Validate positive amount
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Transfer amount must be positive");
    }

    lock.lock();  // Ensure thread safety by locking
    try {
      // Fetch accounts from the repository
      Account accountFrom = accountsRepository.getAccount(accountFromId);
      Account accountTo = accountsRepository.getAccount(accountToId);

      if (accountFrom == null) {
        throw new AccountNotFoundException("Account not found: " + accountFromId);
      }
      if (accountTo == null) {
        throw new AccountNotFoundException("Account not found: " + accountToId);
      }

      // Ensure accountFrom has sufficient balance
      if (accountFrom.getBalance().compareTo(amount) < 0) {
        throw new InsufficientBalanceException("Insufficient balance in account: " + accountFromId);
      }

      // Perform the transfer
      accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
      accountTo.setBalance(accountTo.getBalance().add(amount));

      // Notify both account holders
      notificationService.notifyAboutTransfer(accountFrom, "Transferred " + amount + " to account " + accountToId);
      notificationService.notifyAboutTransfer(accountTo, "Received " + amount + " from account " + accountFromId);

    } finally {
      lock.unlock();  // Always unlock to avoid deadlock
    }
  }
}
