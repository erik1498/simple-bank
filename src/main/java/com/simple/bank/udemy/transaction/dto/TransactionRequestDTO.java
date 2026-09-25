package com.simple.bank.udemy.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.simple.bank.udemy.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequestDTO {
    private TransactionType transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String description;
    private String destinationAccountNumber;
}
