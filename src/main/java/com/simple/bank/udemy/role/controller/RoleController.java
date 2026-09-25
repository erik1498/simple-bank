package com.simple.bank.udemy.role.controller;

import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.role.entity.RoleEntity;
import com.simple.bank.udemy.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Response<RoleEntity>> createRole(@RequestBody RoleEntity request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PutMapping
    public ResponseEntity<Response<RoleEntity>> updateRole(@RequestBody RoleEntity request) {
        return ResponseEntity.ok(roleService.updateRole(request));
    }

    @GetMapping
    public ResponseEntity<Response<List<RoleEntity>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}
