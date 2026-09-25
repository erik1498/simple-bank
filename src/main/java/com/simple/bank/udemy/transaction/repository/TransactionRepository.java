package com.simple.bank.udemy.transaction.repository;

import com.simple.bank.udemy.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Page<TransactionEntity> findByAccount_AccountNumber(String accountNumber, Pageable pageable);
    List<TransactionEntity> findByAccount_AccountNumber(String accountNumber);
}
