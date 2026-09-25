package com.simple.bank.udemy.account.service.impl;

import com.simple.bank.udemy.account.dto.AccountDTO;
import com.simple.bank.udemy.account.entity.AccountEntity;
import com.simple.bank.udemy.account.repository.AccountRepository;
import com.simple.bank.udemy.account.service.AccountService;
import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.auth.service.UserService;
import com.simple.bank.udemy.enums.AccountStatus;
import com.simple.bank.udemy.enums.AccountType;
import com.simple.bank.udemy.enums.Currency;
import com.simple.bank.udemy.exception.BadRequestException;
import com.simple.bank.udemy.exception.NotFoundException;
import com.simple.bank.udemy.res.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService
{

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    @Override
    public AccountEntity createAccount(AccountType accountType, UserEntity userEntity) {
        String accountNumber = generateAccountNumber();
        AccountEntity account = AccountEntity.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currency(Currency.USD)
                .balance(BigDecimal.ZERO)
                .user(userEntity)
                .build();

        return accountRepository.save(account);
    }

    @Override
    public Response<List<AccountDTO>> getMyAccounts() {
        UserEntity user = userService.getCurrentLoggedInUser();

        List<AccountDTO> accountDTOS = accountRepository.findByUserId(user.getId())
                .stream()
                .map(account -> modelMapper.map(account, AccountDTO.class))
                .toList();

        return Response.<List<AccountDTO>>builder()
                .message("User accounts fetched successfully")
                .data(accountDTOS)
                .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {
        UserEntity user = userService.getCurrentLoggedInUser();
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!user.getAccounts().contains(account)) {
            throw new NotFoundException("Account doesn't belong to you");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Account balance must be zero before closing");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        accountRepository.save(account);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account closed successfully")
                .build();
    }

    private String generateAccountNumber() {
        String accountNumber;

        do {
            accountNumber = "66" + (random.nextInt(90000000) + 1000000);
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }
}
