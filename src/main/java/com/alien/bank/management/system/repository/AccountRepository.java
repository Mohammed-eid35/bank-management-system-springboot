package com.alien.bank.management.system.repository;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByCardNumber(String cardNumber);

    List<Account> findAllByUser(User user);

    Optional<Account> findByCardNumber(String cardNumber);

    Optional<Account> findByCardNumberAndCvv(String cardNumber, String cvv);
}
