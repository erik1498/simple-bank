package com.simple.bank.udemy.role.service;

import com.simple.bank.udemy.exception.BadRequestException;
import com.simple.bank.udemy.exception.NotFoundException;
import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.role.entity.RoleEntity;
import com.simple.bank.udemy.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    @Override
    public Response<RoleEntity> createRole(RoleEntity roleRequest) {
        if (roleRepository.findByName(roleRequest.getName()).isPresent()) {
            throw new BadRequestException("Role already exist");
        }

        RoleEntity savedRole = roleRepository.save(roleRequest);

        return Response.<RoleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role saved successfully")
                .data(savedRole)
                .build();
    }

    @Override
    public Response<RoleEntity> updateRole(RoleEntity roleRequest) {
        RoleEntity role = roleRepository.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        role.setName(roleRequest.getName());

        RoleEntity updatedRole = roleRepository.save(role);

        return Response.<RoleEntity>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public Response<List<RoleEntity>> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAll();

        return Response.<List<RoleEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role fetch successfully")
                .data(roles)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role not found");
        }

        roleRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
