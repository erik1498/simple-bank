package com.simple.bank.udemy.auth.service;

import com.simple.bank.udemy.auth.dto.LoginRequestDTO;
import com.simple.bank.udemy.auth.dto.LoginResponse;
import com.simple.bank.udemy.auth.dto.PasswordResetCodeRequestDTO;
import com.simple.bank.udemy.auth.dto.RegistrationRequestDTO;
import com.simple.bank.udemy.res.Response;

public interface AuthService {
    Response<String> register(RegistrationRequestDTO registrationRequestDTO);
    Response<LoginResponse> login(LoginRequestDTO loginRequestDTO);
    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(PasswordResetCodeRequestDTO passwordResetCodeRequestDTO);
}
