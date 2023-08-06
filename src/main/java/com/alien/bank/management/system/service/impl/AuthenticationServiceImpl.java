package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Role;
import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.model.authentication.AuthenticationResponseModel;
import com.alien.bank.management.system.model.authentication.LoginRequestModel;
import com.alien.bank.management.system.model.authentication.RegisterRequestModel;
import com.alien.bank.management.system.model.authentication.UserProfileResponseModel;
import com.alien.bank.management.system.repository.UserRepository;
import com.alien.bank.management.system.security.JwtService;
import com.alien.bank.management.system.service.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthenticationResponseModel register(RegisterRequestModel request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EntityExistsException(request.getEmail() + " is already exists");

        if (userRepository.existsByPhone(request.getPhone()))
            throw new EntityExistsException(request.getPhone() + " is already exists");

        User user = User.builder()
                .name(request.getName())
                .role(Role.USER)
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return AuthenticationResponseModel
                .builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    @Override
    public AuthenticationResponseModel login(LoginRequestModel request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return AuthenticationResponseModel
                .builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
