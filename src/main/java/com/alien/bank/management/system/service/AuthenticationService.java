package com.alien.bank.management.system.service;

import com.alien.bank.management.system.model.authentication.AuthenticationResponseModel;
import com.alien.bank.management.system.model.authentication.RegisterRequestModel;
import com.alien.bank.management.system.model.authentication.LoginRequestModel;
import com.alien.bank.management.system.model.authentication.UserProfileResponseModel;

public interface AuthenticationService {
    public AuthenticationResponseModel register(RegisterRequestModel request);

    public AuthenticationResponseModel login(LoginRequestModel request);
}
