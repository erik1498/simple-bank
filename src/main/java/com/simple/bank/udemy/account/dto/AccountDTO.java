package com.simple.bank.udemy.account.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.simple.bank.udemy.auth.dto.UserDTO;
import com.simple.bank.udemy.enums.AccountStatus;
import com.simple.bank.udemy.enums.AccountType;
import com.simple.bank.udemy.enums.Currency;
import com.simple.bank.udemy.transaction.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;

    @JsonBackReference
    private UserDTO user;
    private Currency currency;
    private AccountStatus status;

    @JsonManagedReference
    private List<TransactionDTO> transactions = new ArrayList<>();

    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
