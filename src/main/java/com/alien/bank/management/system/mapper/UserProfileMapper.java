package com.alien.bank.management.system.mapper;

import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.model.authentication.UserProfileResponseModel;

public interface UserProfileMapper {
    UserProfileResponseModel toUserProfile(User user);
}
