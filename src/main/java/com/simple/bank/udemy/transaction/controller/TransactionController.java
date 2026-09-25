package com.simple.bank.udemy.transaction.controller;

import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.transaction.dto.TransactionRequestDTO;
import com.simple.bank.udemy.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionRequestDTO));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Response<?>> getTransactionForMyAccount(@PathVariable("accountNumber") String accountNumber, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.getTransactionForMyAccount(accountNumber,page, size));
    }
}
