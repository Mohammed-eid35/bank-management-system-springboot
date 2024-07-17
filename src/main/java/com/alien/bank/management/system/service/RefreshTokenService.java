package com.alien.bank.management.system.service;

import com.alien.bank.management.system.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    String createRefreshToken(String username);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUsername(String username);
}
