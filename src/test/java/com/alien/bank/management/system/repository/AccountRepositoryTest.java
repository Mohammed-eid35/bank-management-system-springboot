package com.alien.bank.management.system.repository;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.entity.Role;
import com.alien.bank.management.system.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccountRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    private User user;
    private Account account;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(User
                .builder()
                .name("Muhammad Eid")
                .email("mohammed@gmail.com")
                .phone("01552422396")
                .role(Role.USER)
                .password("123456")
                .build()
        );

        account = accountRepository.save(Account
                .builder()
                .cardNumber("1234567890123456")
                .cvv("123")
                .balance(10000.0)
                .user(user)
                .build()
        );

        accountRepository.save(Account
                .builder()
                .cardNumber("1234567890125556")
                .cvv("123")
                .balance(10000.0)
                .user(user)
                .build()
        );

        accountRepository.save(Account
                .builder()
                .cardNumber("1234997890123456")
                .cvv("123")
                .balance(10000.0)
                .user(user)
                .build()
        );
    }

    @Test
    public void shouldExistsByCardNumber() {
        Boolean exists = accountRepository.existsByCardNumber(account.getCardNumber());
        assertThat(exists).isTrue();
    }

    @Test
    public void shouldNotExistsByCardNumberWhenAccountDoesNotExist() {
        Boolean exists = accountRepository.existsByCardNumber("invalid-card-number");
        assertThat(exists).isFalse();
    }

    @Test
    public void shouldFindAllByUser() {
        List<Account> accounts = accountRepository.findAllByUser(user);
        assertThat(accounts).hasSize(3);
    }

    @Test
    public void shouldReturnEmptyListWhenUserDoesNotExist() {
        List<Account> accounts = accountRepository.findAllByUser(User
                .builder()
                .id(5L)
                .name("user user")
                .email("user@gmail.com")
                .phone("15524223960")
                .role(Role.USER)
                .password("123456")
                .build()
        );
        assertThat(accounts).isEmpty();
    }

    @Test
    public void shouldFindByCardNumber() {
        Optional<Account> account = accountRepository.findByCardNumber(this.account.getCardNumber());
        assertThat(account).isPresent();
        assertThat(account.get()).isEqualTo(accountRepository.findAllByUser(user).get(0));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenAccountDoesNotExistByCardNumber() {
        Optional<Account> account = accountRepository.findByCardNumber("invalid-card-number");
        assertThat(account).isEmpty();
    }

    @Test
    public void shouldFindByCardNumberAndCvv() {
        Optional<Account> account = accountRepository.findByCardNumberAndCvv(this.account.getCardNumber(), this.account.getCvv());
        assertThat(account).isPresent();
        assertThat(account.get()).isEqualTo(accountRepository.findAllByUser(user).get(0));
    }

    @Test
    public void shouldReturnEmptyOptionalWhenAccountDoesNotExistByCardNumberAndCvv() {
        Optional<Account> account = accountRepository.findByCardNumberAndCvv("invalid-card-number", "123");
        assertThat(account).isEmpty();
    }
}
