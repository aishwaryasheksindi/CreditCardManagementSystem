package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleDto;

public interface IRoleService {

    RoleDto addRole(RoleDto roleDto);

    RoleDto getRoleById(String roleId);
}