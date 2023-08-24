package com.alien.bank.management.system.service.impl;

import com.alien.bank.management.system.entity.Role;
import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.mapper.UserProfileMapper;
import com.alien.bank.management.system.model.authentication.UserProfileResponseModel;
import com.alien.bank.management.system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserProfileMapper userProfileMapper;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl(userRepository, userProfileMapper);
    }

    @Test
    public void getUserProfileShouldReturnUserDataSuccessfully() {
        String email = "mohammed@gmail.com";
        User user = User.
                builder()
                .id(1L)
                .name("Muhammad Eid")
                .email(email)
                .phone("01552422396")
                .password("123456")
                .role(Role.USER)
                .build();

        UserProfileResponseModel userProfile = UserProfileResponseModel
                .builder()
                .email(email)
                .name(user.getName())
                .phone(user.getPhone())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userProfileMapper.toUserProfile(user)).thenReturn(userProfile);

        UserProfileResponseModel response = userService.getUserProfile();

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(user.getName());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getPhone()).isEqualTo(user.getPhone());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserProfile_whenUserNotFound_shouldThrowEntityNotFoundException() {
        String email = "mohammed@gmail.com";

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile())
                .isInstanceOf(EntityNotFoundException.class);

        verify(userProfileMapper, never()).toUserProfile(any(User.class));
    }
}
