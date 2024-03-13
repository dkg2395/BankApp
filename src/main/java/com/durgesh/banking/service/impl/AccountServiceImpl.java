package com.durgesh.banking.service.impl;

import org.springframework.stereotype.Service;

import com.durgesh.banking.dto.AccountDto;
import com.durgesh.banking.dto.TransferFundDto;
import com.durgesh.banking.entity.Account;
import com.durgesh.banking.exception.AccountException;
import com.durgesh.banking.mapper.AccountMapper;
import com.durgesh.banking.repository.AccountRepository;
import com.durgesh.banking.service.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient amount");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        accountRepository.deleteById(id);
    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {

        // Retrieve the account from which we send the amount
        Account fromAccount = accountRepository
                .findById(transferFundDto.fromAccountId())
                .orElseThrow(() -> new AccountException("Account does not exists"));

        // Retrieve the account to which we send the amount
        Account toAccount = accountRepository.findById(transferFundDto.toAccountId())
                .orElseThrow(() -> new AccountException("Account does not exists"));

        // Debit the amount from fromAccount object
        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());

        // Credit the amount to toAccount object
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

        accountRepository.save(fromAccount);

        accountRepository.save(toAccount);
    }
}
