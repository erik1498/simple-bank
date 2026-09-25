package com.simple.bank.udemy.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.simple.bank.udemy.account.dto.AccountDTO;
import com.simple.bank.udemy.role.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    @JsonIgnore
    private String password;
    private String profilePictureUrl;
    private boolean active = true;
    private List<RoleEntity> roles;

    @JsonManagedReference
    private List<AccountDTO> accounts;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
