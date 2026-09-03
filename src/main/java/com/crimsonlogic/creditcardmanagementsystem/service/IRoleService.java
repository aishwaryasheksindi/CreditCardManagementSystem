package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RoleResponseDto;

public interface IRoleService {

    RoleResponseDto addRole(RoleRequestDto roleRequestDto);

    RoleResponseDto getRoleById(String roleId);
}