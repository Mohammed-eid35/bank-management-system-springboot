package com.alien.bank.management.system.repository;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

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
    }

    @Test
    public void shouldFindUserByEmail() {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenUserDoesNotExistByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("invalid-email@example.com");
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void shouldExistsByEmail() {
        Boolean exists = userRepository.existsByEmail(user.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    public void shouldNotExistsByEmailWhenUserDoesNotExist() {
        Boolean exists = userRepository.existsByEmail("invalid-email@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    public void shouldExistsByPhone() {
        Boolean exists = userRepository.existsByPhone(user.getPhone());
        assertThat(exists).isTrue();
    }

    @Test
    public void shouldNotExistsByPhoneWhenUserDoesNotExist() {
        Boolean exists = userRepository.existsByPhone("01152422396");
        assertThat(exists).isFalse();
    }
}