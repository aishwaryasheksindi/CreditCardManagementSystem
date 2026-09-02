package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleDto;
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
    public RoleDto addRole(RoleDto roleDto) {

        Role role = new Role();

        role.setRoleId(IdGenerationUtil.generateRoleId());
        role.setRoleName(roleDto.getRoleName());
        role.setDescription(roleDto.getDescription());

        Role savedRole = roleRepository.save(role);

        RoleDto responseDto = new RoleDto();

        responseDto.setRoleId(savedRole.getRoleId());
        responseDto.setRoleName(savedRole.getRoleName());
        responseDto.setDescription(savedRole.getDescription());

        return responseDto;
    }

    @Override
    public RoleDto getRoleById(String roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        RoleDto responseDto = new RoleDto();

        responseDto.setRoleId(role.getRoleId());
        responseDto.setRoleName(role.getRoleName());
        responseDto.setDescription(role.getDescription());

        return responseDto;
    }
}