package com.simple.bank.udemy.auth.controller;

import com.simple.bank.udemy.auth.dto.LoginRequestDTO;
import com.simple.bank.udemy.auth.dto.LoginResponse;
import com.simple.bank.udemy.auth.dto.PasswordResetCodeRequestDTO;
import com.simple.bank.udemy.auth.dto.RegistrationRequestDTO;
import com.simple.bank.udemy.auth.service.AuthService;
import com.simple.bank.udemy.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequestDTO registrationRequestDTO) {
        return ResponseEntity.ok(authService.register(registrationRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestBody @Valid PasswordResetCodeRequestDTO passwordResetCodeRequestDTO) {
        return ResponseEntity.ok(authService.forgetPassword(passwordResetCodeRequestDTO.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody PasswordResetCodeRequestDTO passwordResetCodeRequestDTO) {
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(passwordResetCodeRequestDTO));
    }
}
