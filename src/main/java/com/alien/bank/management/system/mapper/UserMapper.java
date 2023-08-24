package com.alien.bank.management.system.mapper;

import com.alien.bank.management.system.entity.User;
import com.alien.bank.management.system.model.authentication.RegisterRequestModel;

public interface UserMapper {
    User toUser(RegisterRequestModel request);
}
