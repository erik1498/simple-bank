package com.simple.bank.udemy.auth.repository;

import com.simple.bank.udemy.auth.entity.PasswordResetCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCodeEntity, Long> {
    Optional<PasswordResetCodeEntity> findByCode(String code);
    void deleteByUserId(Long userId);
}
