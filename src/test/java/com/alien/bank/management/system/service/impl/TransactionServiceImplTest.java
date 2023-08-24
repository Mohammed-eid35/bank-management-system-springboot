package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.*;
import com.alien.bank.management.system.exception.LowBalanceException;
import com.alien.bank.management.system.mapper.TransactionMapper;
import com.alien.bank.management.system.model.transaction.DepositRequestModel;
import com.alien.bank.management.system.model.transaction.TransactionResponseModel;
import com.alien.bank.management.system.model.transaction.WithdrawRequestModel;
import com.alien.bank.management.system.repository.AccountRepository;
import com.alien.bank.management.system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;


    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account;
    private DepositRequestModel depositRequest;
    private WithdrawRequestModel withdrawRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        transactionService = new TransactionServiceImpl(accountRepository, transactionRepository, transactionMapper);

        account = Account
                .builder()
                .id(1L)
                .cardNumber("1234567890123456")
                .cvv("123")
                .balance(1000.0)
                .user(User
                        .builder()
                        .id(1L)
                        .name("Muhammad Eid")
                        .role(Role.USER)
                        .email("mohammed@gmail.com")
                        .phone("01552422396")
                        .password("123456")
                        .build()
                )
                .build();

        depositRequest = DepositRequestModel
                .builder()
                .card_number("1234567890123456")
                .amount(100.0)
                .build();

        withdrawRequest = WithdrawRequestModel
                .builder()
                .card_number("1234567890123456")
                .cvv("123")
                .amount(100.0)
                .build();
    }

    @Test
    public void shouldDepositSuccessfully() {
        Transaction transaction = Transaction
                .builder()
                .id(1L)
                .amount(depositRequest.getAmount())
                .account(account)
                .type(TransactionType.DEPOSIT)
                .timestamp(new Date())
                .notes("Account Balance: " + (account.getBalance() + depositRequest.getAmount()))
                .build();


        when(accountRepository.findByCardNumber(depositRequest.getCard_number())).thenReturn(Optional.of(account));
        when(transactionMapper.toEntity(depositRequest.getAmount(), account, TransactionType.DEPOSIT)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponseModel expectedResponse = TransactionResponseModel
                .builder()
                .id(1L)
                .balance(account.getBalance() + depositRequest.getAmount())
                .amount(depositRequest.getAmount())
                .build();

        when(transactionMapper
                .toResponseModel(transaction.getId(), depositRequest.getAmount(), account.getBalance() + depositRequest.getAmount())
        ).thenReturn(expectedResponse);


        TransactionResponseModel response = transactionService.deposit(depositRequest);

        assertThat(response).isNotNull();

        assertThat(response.getBalance()).isEqualTo(expectedResponse.getBalance());
        assertThat(response.getAmount()).isEqualTo(expectedResponse.getAmount());
        assertThat(response.getId()).isEqualTo(expectedResponse.getId());

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void depositShouldThrowBadCredentialsExceptionWhenAccountWithCardNumberNotFound() {
        when(accountRepository.findByCardNumber(depositRequest.getCard_number())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deposit(depositRequest)).isInstanceOf(BadCredentialsException.class);

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void shouldWithdrawSuccessfully() {
        Transaction transaction = Transaction
                .builder()
                .id(1L)
                .amount(withdrawRequest.getAmount())
                .account(account)
                .type(TransactionType.WITHDRAW)
                .timestamp(new Date())
                .notes("Account Balance: " + (account.getBalance() - withdrawRequest.getAmount()))
                .build();

        when(accountRepository
                .findByCardNumberAndCvv(withdrawRequest.getCard_number(), withdrawRequest.getCvv())
        ).thenReturn(Optional.of(account));
        when(transactionMapper.toEntity(withdrawRequest.getAmount(), account, TransactionType.WITHDRAW)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponseModel expectedResponse = TransactionResponseModel
                .builder()
                .id(1L)
                .balance(account.getBalance() - withdrawRequest.getAmount())
                .amount(withdrawRequest.getAmount())
                .build();

        when(transactionMapper
                .toResponseModel(transaction.getId(), withdrawRequest.getAmount(), account.getBalance() - withdrawRequest.getAmount())
        ).thenReturn(expectedResponse);

        TransactionResponseModel response = transactionService.withdraw(withdrawRequest);


        assertThat(response).isNotNull();

        assertThat(response.getBalance()).isEqualTo(expectedResponse.getBalance());
        assertThat(response.getAmount()).isEqualTo(expectedResponse.getAmount());
        assertThat(response.getId()).isEqualTo(expectedResponse.getId());

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void withdrawShouldThrowBadCredentialsExceptionWhenAccountWithCardNumberAndCvvNotFound() {
        when(accountRepository
                .findByCardNumberAndCvv(withdrawRequest.getCard_number(), withdrawRequest.getCvv())
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.withdraw(withdrawRequest)).isInstanceOf(BadCredentialsException.class);

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void withdrawShouldThrowLowBalanceExceptionWhenInsufficientBalance() {
        withdrawRequest.setAmount(10000.0);

        when(accountRepository
                .findByCardNumberAndCvv(withdrawRequest.getCard_number(), withdrawRequest.getCvv())
        ).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw(withdrawRequest)).isInstanceOf(LowBalanceException.class);

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
