package com.simple.bank.udemy.auth.service;

import com.simple.bank.udemy.auth.dto.UpdatePasswordRequestDTO;
import com.simple.bank.udemy.auth.dto.UserDTO;
import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.res.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserEntity getCurrentLoggedInUser();
    Response<UserDTO> getMyProfile();
    Response<Page<UserDTO>> getAllUsers(int page, int size);
    Response<?> updatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO);
    Response<?> uploadProfilePicture(MultipartFile file);
}
