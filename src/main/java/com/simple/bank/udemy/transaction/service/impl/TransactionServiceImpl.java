package com.simple.bank.udemy.transaction.service.impl;

import com.simple.bank.udemy.account.entity.AccountEntity;
import com.simple.bank.udemy.account.repository.AccountRepository;
import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.auth.service.UserService;
import com.simple.bank.udemy.enums.TransactionStatus;
import com.simple.bank.udemy.enums.TransactionType;
import com.simple.bank.udemy.exception.BadRequestException;
import com.simple.bank.udemy.exception.InsufficientBalanceException;
import com.simple.bank.udemy.exception.InvalidTransactionException;
import com.simple.bank.udemy.exception.NotFoundException;
import com.simple.bank.udemy.notification.dto.NotificationDTO;
import com.simple.bank.udemy.notification.service.NotificationService;
import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.transaction.dto.TransactionDTO;
import com.simple.bank.udemy.transaction.dto.TransactionRequestDTO;
import com.simple.bank.udemy.transaction.entity.TransactionEntity;
import com.simple.bank.udemy.transaction.repository.TransactionRepository;
import com.simple.bank.udemy.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequestDTO transactionRequestDTO) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionType(transactionRequestDTO.getTransactionType());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setDescription(transactionRequestDTO.getDescription());

        switch (transactionRequestDTO.getTransactionType()) {
            case DEPOSIT -> handleDeposit(transactionRequestDTO, transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequestDTO, transaction);
            case TRANSFER -> handleTransfer(transactionRequestDTO, transaction);
            default -> throw new InvalidTransactionException("Invalid transaction type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        sendTransactionNotification(savedTransaction);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transaction successfully")
                .build();
    }

    @Override
    @Transactional
    public Response<List<TransactionDTO>> getTransactionForMyAccount(String accountNumber, int page, int size) {
        UserEntity user = userService.getCurrentLoggedInUser();

        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Account does not belong to the authenticated user");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

        Page<TransactionEntity> transactionEntities = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);

        List<TransactionDTO> transactionDTOS = transactionEntities.getContent().stream()
                .map(transactionEntity -> modelMapper.map(transactionEntity, TransactionDTO.class))
                .toList();

        return Response.<List<TransactionDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transaction retrieved")
                .data(transactionDTOS)
                .meta(Map.of(
                        "currentPage", transactionEntities.getNumber(),
                        "totalItems", transactionEntities.getTotalElements(),
                        "totalPages", transactionEntities.getTotalPages(),
                        "pageSize", transactionEntities.getSize()
                ))
                .build();
    }

    private void handleDeposit(TransactionRequestDTO transactionRequestDTO, TransactionEntity transactionEntity) {
        AccountEntity account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber()).orElseThrow(() -> new NotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(transactionRequestDTO.getAmount()));
        transactionEntity.setAccount(account);

        accountRepository.save(account);
    }

    private void handleWithdrawal(TransactionRequestDTO transactionRequestDTO, TransactionEntity transactionEntity) {
        AccountEntity account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber()).orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(transactionRequestDTO.getAmount()));
        transactionEntity.setAccount(account);

        accountRepository.save(account);
    }

    private void handleTransfer(TransactionRequestDTO transactionRequestDTO, TransactionEntity transactionEntity) {
        AccountEntity account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber()).orElseThrow(() -> new NotFoundException("Account not found"));

        AccountEntity destinationAccount = accountRepository.findByAccountNumber(transactionRequestDTO.getDestinationAccountNumber()).orElseThrow(() -> new NotFoundException("Destination account not found"));

        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(transactionRequestDTO.getAmount()));
        accountRepository.save(account);

        destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequestDTO.getAmount()));
        accountRepository.save(destinationAccount);

        transactionEntity.setAccount(account);
        transactionEntity.setSourceAccount(account.getAccountNumber());
        transactionEntity.setDestinationAccount(destinationAccount.getAccountNumber());
    }

    private void sendTransactionNotification(TransactionEntity transactionEntity) {
        UserEntity user = transactionEntity.getAccount().getUser();

        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("amount", transactionEntity.getAmount());
        templateVariables.put("accountNumber", transactionEntity.getAccount().getAccountNumber());
        templateVariables.put("date", transactionEntity.getTransactionDate());
        templateVariables.put("balance", transactionEntity.getAccount().getBalance());

        if (transactionEntity.getTransactionType() == TransactionType.DEPOSIT) {
            subject = "Credit Alert";
            template = "credit-alert";

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);
        } else if (transactionEntity.getTransactionType() == TransactionType.WITHDRAWAL) {
            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);
        } else if (transactionEntity.getTransactionType() == TransactionType.TRANSFER) {
            subject = "Debit Alert";
            template = "debit-alert";

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);

            AccountEntity destination = accountRepository.findByAccountNumber(transactionEntity.getDestinationAccount()).orElseThrow(() -> new NotFoundException("Destination account not found"));

            UserEntity receiver = destination.getUser();

            Map<String,Object> recVars = new HashMap<>();
            recVars.put("name", receiver.getFirstName());
            recVars.put("amount", transactionEntity.getAmount());
            recVars.put("accountNumber", destination.getAccountNumber());
            recVars.put("date", transactionEntity.getTransactionDate());
            recVars.put("balance", destination.getBalance());

            NotificationDTO notificationDTOSendOutToReceiver = NotificationDTO.builder()
                    .recipient(receiver.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(recVars)
                    .build();

            notificationService.sendEmail(notificationDTOSendOutToReceiver, receiver);
        }

    }
}
