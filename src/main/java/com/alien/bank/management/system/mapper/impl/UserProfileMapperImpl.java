package com.alien.bank.management.system.mapper.impl;

import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.mapper.UserProfileMapper;
import com.alien.bank.management.system.model.authentication.UserProfileResponseModel;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapperImpl implements UserProfileMapper {
    @Override
    public UserProfileResponseModel toUserProfile(User user) {
        return UserProfileResponseModel
                .builder()
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
