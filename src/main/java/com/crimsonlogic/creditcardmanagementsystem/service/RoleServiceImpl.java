package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RoleResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponseDto addRole(RoleRequestDto roleRequestDto) {

        Role role = new Role();

        role.setRoleId(IdGenerationUtil.generateRoleId());
        role.setRoleName(roleRequestDto.getRoleName());
        role.setDescription(roleRequestDto.getDescription());

        Role savedRole = roleRepository.save(role);

        RoleResponseDto responseDto = new RoleResponseDto();

        responseDto.setRoleId(savedRole.getRoleId());
        responseDto.setRoleName(savedRole.getRoleName());
        responseDto.setDescription(savedRole.getDescription());

        return responseDto;
    }

    @Override
    public RoleResponseDto getRoleById(String roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        RoleResponseDto responseDto = new RoleResponseDto();

        responseDto.setRoleId(role.getRoleId());
        responseDto.setRoleName(role.getRoleName());
        responseDto.setDescription(role.getDescription());

        return responseDto;
    }
}