package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.RefreshToken;
import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.exception.RefreshTokenNotValidException;
import com.alien.bank.management.system.mapper.UserMapper;
import com.alien.bank.management.system.model.authentication.AuthenticationResponseModel;
import com.alien.bank.management.system.model.authentication.LoginRequestModel;
import com.alien.bank.management.system.model.authentication.RegisterRequestModel;
import com.alien.bank.management.system.repository.UserRepository;
import com.alien.bank.management.system.security.JwtService;
import com.alien.bank.management.system.service.AuthenticationService;
import com.alien.bank.management.system.service.RefreshTokenService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;


    @Override
    public AuthenticationResponseModel register(RegisterRequestModel request) {
        if (isEmailOrPhoneAlreadyExists(request.getEmail(), request.getPhone())) {
            throw new EntityExistsException("Email or Phone Number is already exists");
        }

        User user = userRepository.save(userMapper.toUser(request));

        return AuthenticationResponseModel.builder().token(jwtService.generateToken(user)).build();
    }

    @Override
    public AuthenticationResponseModel login(LoginRequestModel request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User " + request.getEmail() + " Not Found"));
        String refreshToken = refreshTokenService.createRefreshToken(username);
        return AuthenticationResponseModel
                .builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshToken)
                .build();
    }


    @Override
    public AuthenticationResponseModel refreshToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);

        if (refreshTokenOptional.isEmpty()) {
            throw new EntityNotFoundException("Refresh token not in database");
        }

        RefreshToken token = refreshTokenOptional.get();

        try {
            token = refreshTokenService.verifyExpiration(token);
        } catch (RuntimeException e) {
            throw new RefreshTokenNotValidException("Refresh token was expired. Please make a new signin request");
        }

        String username = token.getUsername();
        String newAccessToken = jwtService.generateRefreshToken(username);

        return AuthenticationResponseModel
                .builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenService.deleteByUsername(token.getUsername()));
    }
    private boolean isEmailOrPhoneAlreadyExists(String email, String phone) {
        return userRepository.existsByEmail(email) || userRepository.existsByPhone(phone);
    }
}
