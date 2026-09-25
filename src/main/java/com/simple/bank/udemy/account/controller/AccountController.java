package com.simple.bank.udemy.account.controller;

import com.simple.bank.udemy.account.dto.AccountDTO;
import com.simple.bank.udemy.account.service.AccountService;
import com.simple.bank.udemy.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<Response<List<AccountDTO>>> getMyAccounts() {
        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    @DeleteMapping("/close/{accountNumber}")
    public ResponseEntity<Response<?>> closeAccount(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }
}
