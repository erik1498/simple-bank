package com.simple.bank.udemy.transaction.service;

import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.transaction.dto.TransactionDTO;
import com.simple.bank.udemy.transaction.dto.TransactionRequestDTO;

import java.util.List;

public interface TransactionService {
    Response<?> createTransaction(TransactionRequestDTO transactionRequestDTO);
    Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page, int size);
}
