package com.alien.bank.management.system.service;

import com.alien.bank.management.system.model.account.AccountResponseModel;

import java.net.URI;
import java.util.List;

public interface AccountService {
    AccountResponseModel createNewAccount();

    List<AccountResponseModel> getMyAccounts();
}
