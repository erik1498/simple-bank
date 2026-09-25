package com.simple.bank.udemy.audit_dashboard.service;

import com.simple.bank.udemy.account.dto.AccountDTO;
import com.simple.bank.udemy.auth.dto.UserDTO;
import com.simple.bank.udemy.transaction.dto.TransactionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditorService {
    Map<String, Long> getSystemTotal();
    Optional<UserDTO> findUserByEmail(String email);
    Optional<AccountDTO> findAccountByAccountNumber(String accountNumber);
    List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber);
    Optional<TransactionDTO> findTransactionById(Long transactionId);
}
