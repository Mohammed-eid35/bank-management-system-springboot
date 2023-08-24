package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.entity.Role;
import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.mapper.AccountMapper;
import com.alien.bank.management.system.model.account.AccountResponseModel;
import com.alien.bank.management.system.repository.AccountRepository;
import com.alien.bank.management.system.repository.UserRepository;
import com.alien.bank.management.system.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User user;
    private Account account;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        accountService = new AccountServiceImpl(userRepository, accountRepository, accountMapper);

        user = User.
                builder()
                .id(1L)
                .name("Muhammad Eid")
                .email("mohammed@gmail.com")
                .phone("01552422396")
                .password("123456")
                .role(Role.USER)
                .build();

        account = Account
                .builder()
                .id(1L)
                .cvv("123")
                .cardNumber("0123456789123456")
                .build();
    }

    @Test
    public void createNewAccountShouldCreateNewAccountSuccessfully() {
        AccountResponseModel expectedResponse = AccountResponseModel
                .builder()
                .balance(0.0)
                .card_number(account.getCardNumber())
                .cvv(account.getCvv())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toResponseModel(account)).thenReturn(expectedResponse);

        AccountResponseModel response = accountService.createNewAccount();

        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(expectedResponse.getBalance());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void createNewAccountShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createNewAccount())
                .isInstanceOf(EntityNotFoundException.class);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void getMyAccountsSuccessfully() {
        AccountResponseModel accountResponse = AccountResponseModel
                .builder()
                .balance(0.0)
                .card_number(account.getCardNumber())
                .cvv(account.getCvv())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.findAllByUser(user)).thenReturn(Collections.singletonList(account));
        when(accountMapper.toResponseModel(account)).thenReturn(accountResponse);

        List<AccountResponseModel> response = accountService.getMyAccounts();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response).containsExactly(accountResponse);

        verify(accountRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void getMyAccountsShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getMyAccounts())
                .isInstanceOf(EntityNotFoundException.class);

        verify(accountRepository, never()).findAllByUser(user);
    }

    @Test
    void generateUniqueCardNumber_FirstAttempt() {
        String cardNumber = "1234567890123456";

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {
            utilsMockedStatic.when(Utils::generateCardNumber).thenReturn(cardNumber);

            when(accountRepository.existsByCardNumber(cardNumber)).thenReturn(false);

            String uniqueCardNumber = accountService.generateUniqueCardNumber();

            assertThat(uniqueCardNumber).isNotNull();
            assertThat(uniqueCardNumber).isEqualTo(cardNumber);
        }
    }

    @Test
    void generateUniqueCardNumber_SecondAttempt() {
        String cardNumber1 = "1234567890123456";
        String cardNumber2 = "1596321596321596";

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {
            utilsMockedStatic.when(Utils::generateCardNumber).thenReturn(cardNumber1, cardNumber2);

            when(accountRepository.existsByCardNumber(anyString())).thenReturn(true, false);

            String uniqueCardNumber = accountService.generateUniqueCardNumber();

            assertThat(uniqueCardNumber).isNotNull();
            assertThat(uniqueCardNumber).isEqualTo(cardNumber2);

            verify(accountRepository, times(2)).existsByCardNumber(anyString());
        }
    }
}
