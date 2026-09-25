package com.simple.bank.udemy.auth.service.impl;

import com.simple.bank.udemy.account.entity.AccountEntity;
import com.simple.bank.udemy.account.service.AccountService;
import com.simple.bank.udemy.auth.dto.LoginRequestDTO;
import com.simple.bank.udemy.auth.dto.LoginResponse;
import com.simple.bank.udemy.auth.dto.PasswordResetCodeRequestDTO;
import com.simple.bank.udemy.auth.dto.RegistrationRequestDTO;
import com.simple.bank.udemy.auth.entity.PasswordResetCodeEntity;
import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.auth.repository.PasswordResetCodeRepository;
import com.simple.bank.udemy.auth.repository.UserRepository;
import com.simple.bank.udemy.auth.service.AuthService;
import com.simple.bank.udemy.auth.service.CodeGenerator;
import com.simple.bank.udemy.enums.AccountType;
import com.simple.bank.udemy.enums.Currency;
import com.simple.bank.udemy.exception.BadRequestException;
import com.simple.bank.udemy.exception.NotFoundException;
import com.simple.bank.udemy.notification.dto.NotificationDTO;
import com.simple.bank.udemy.notification.service.NotificationService;
import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.role.entity.RoleEntity;
import com.simple.bank.udemy.role.repository.RoleRepository;
import com.simple.bank.udemy.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final AccountService accountService;

    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepository passwordResetCodeRepository;

    @Value("${password.reset.link}")
    private String resetLink;

    @Override
    public Response<String> register(RegistrationRequestDTO registrationRequestDTO) {
        List<RoleEntity> roles;

        if (registrationRequestDTO.getRoles() == null || registrationRequestDTO.getRoles().isEmpty()) {
            RoleEntity defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("CUSTOMER ROLE NOT FOUND"));

            roles = Collections.singletonList(defaultRole);
        } else {
            roles = registrationRequestDTO.getRoles()
                    .stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new NotFoundException("ROLE NOT FOUND " + roleName)))
                    .toList();
        }

        if (userRepository.findByEmail(registrationRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already present");
        }

        UserEntity user = UserEntity.builder()
                .firstName(registrationRequestDTO.getFirstName())
                .lastName(registrationRequestDTO.getLastName())
                .email(registrationRequestDTO.getEmail())
                .phoneNumber(registrationRequestDTO.getPhoneNumber())
                .password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .roles(roles)
                .active(true)
                .build();

        UserEntity savedUser = userRepository.save(user);

        AccountEntity savedAccount = accountService.createAccount(AccountType.SAVINGS, user);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", savedUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to SIMPLE-BANK")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO, savedUser);

        Map<String, Object> accountVars = new HashMap<>();
        accountVars.put("name", savedUser.getFirstName());
        accountVars.put("accountNumber", savedAccount.getAccountNumber());
        accountVars.put("accountType", AccountType.SAVINGS.name());
        accountVars.put("currency", Currency.USD);

        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your new bank account has been created")
                .templateName("account-created")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreatedEmail, savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your account has been created successfully")
                .data("Email of your account details has been sent to you, Your account number is: " + savedAccount.getAccountNumber())
                .build();

    }

    @Override
    public Response<LoginResponse> login(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Email not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Password doesn't match");
        }

        String token = tokenService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(RoleEntity::getName).toList())
                .token(token)
                .build();

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successfully")
                .data(loginResponse)
                .build();
    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {
        UserEntity user =  userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        passwordResetCodeRepository.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCodeEntity resetCode = PasswordResetCodeEntity.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetCodeRepository.save(resetCode);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("resetLink", resetLink + code);

        NotificationDTO notification = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notification, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();
    }

    @Override
    public Response<?> updatePasswordViaResetCode(PasswordResetCodeRequestDTO passwordResetCodeRequestDTO) {
        String code = passwordResetCodeRequestDTO.getCode();
        String newPassword = passwordResetCodeRequestDTO.getNewPassword();

        PasswordResetCodeEntity resetCode = passwordResetCodeRepository.findByCode(code).orElseThrow(() -> new BadRequestException("Invalid reset code"));

        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetCodeRepository.delete(resetCode);
            throw new BadRequestException("Reset code has expired");
        }

        UserEntity user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetCodeRepository.delete(resetCode);

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDTO confirmationEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password updated successfully")
                .templateName("password-update-confirmation")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(confirmationEmail, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }
}
