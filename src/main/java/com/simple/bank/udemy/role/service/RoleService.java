package com.simple.bank.udemy.role.service;

import com.simple.bank.udemy.res.Response;
import com.simple.bank.udemy.role.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    Response<RoleEntity>  createRole(RoleEntity roleRequest);
    Response<RoleEntity>  updateRole(RoleEntity roleRequest);
    Response<List<RoleEntity>>  getAllRoles();
    Response<?> deleteRole(Long id);
}
