package com.simple.bank.udemy.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResetCodeRequestDTO {
    private  String email;
    private String code;
    private String newPassword;
}
