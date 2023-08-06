package com.alien.bank.management.system.mapper;

import com.alien.bank.management.system.entity.Account;
import com.alien.bank.management.system.model.account.AccountResponseModel;

public interface AccountMapper {
    AccountResponseModel toResponseModel(Account account);
}
