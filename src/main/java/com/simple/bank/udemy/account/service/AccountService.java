package com.simple.bank.udemy.account.service;

import com.simple.bank.udemy.account.dto.AccountDTO;
import com.simple.bank.udemy.account.entity.AccountEntity;
import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.enums.AccountType;
import com.simple.bank.udemy.res.Response;

import java.util.List;

public interface AccountService {
    AccountEntity createAccount(AccountType accountType, UserEntity userEntity);
    Response<List<AccountDTO>> getMyAccounts();
    Response<?> closeAccount(String accountNumber);
}
