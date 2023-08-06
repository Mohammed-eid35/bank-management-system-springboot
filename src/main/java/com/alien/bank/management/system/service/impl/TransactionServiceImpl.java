package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.entity.Transaction;
import com.alien.bank.management.system.entity.TransactionType;
import com.alien.bank.management.system.exception.LowBalanceException;
import com.alien.bank.management.system.model.transaction.DepositRequestModel;
import com.alien.bank.management.system.model.transaction.TransactionResponseModel;
import com.alien.bank.management.system.model.transaction.WithdrawRequestModel;
import com.alien.bank.management.system.repository.AccountRepository;
import com.alien.bank.management.system.repository.TransactionRepository;
import com.alien.bank.management.system.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponseModel deposit(DepositRequestModel request) {
        Account account = accountRepository.findByCardNumber(request.getCard_number())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);

        transactionRepository.save(
                Transaction
                        .builder()
                        .amount(request.getAmount())
                        .account(account)
                        .type(TransactionType.DEPOSIT)
                        .timestamp(new Date())
                        .notes("Account Balance" + account.getBalance())
                        .build()
        );

        return TransactionResponseModel
                .builder()
                .amount(request.getAmount())
                .balance(account.getBalance())
                .build();
    }

    @Override
    public TransactionResponseModel withdraw(WithdrawRequestModel request) {
        Account account = accountRepository.findByCardNumberAndCvv(request.getCard_number(), request.getCvv())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        if (account.getBalance() < request.getAmount())
            throw new LowBalanceException("Your Balance " + account.getBalance() + " is not enough to withdraw " + request.getAmount());

        account.setBalance(account.getBalance() - request.getAmount());
        accountRepository.save(account);

        transactionRepository.save(
                Transaction
                        .builder()
                        .amount(request.getAmount())
                        .account(account)
                        .type(TransactionType.WITHDRAW)
                        .timestamp(new Date())
                        .notes("Account Balance" + account.getBalance())
                        .build()
        );

        return TransactionResponseModel
                .builder()
                .amount(request.getAmount())
                .balance(account.getBalance())
                .build();
    }
}