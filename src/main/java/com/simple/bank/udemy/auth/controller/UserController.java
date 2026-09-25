package com.simple.bank.udemy.auth.controller;

import com.simple.bank.udemy.auth.dto.UpdatePasswordRequestDTO;
import com.simple.bank.udemy.auth.dto.UserDTO;
import com.simple.bank.udemy.auth.service.UserService;
import com.simple.bank.udemy.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Page<UserDTO>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getCurrentUser() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/update-password")
    public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequestDTO));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<Response<?>> updateProfilePicture(@RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePicture(file));
    }
}
