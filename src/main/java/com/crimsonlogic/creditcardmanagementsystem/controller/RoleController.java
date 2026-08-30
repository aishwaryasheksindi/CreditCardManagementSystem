package com.crimsonlogic.creditcardmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @PostMapping
    public RoleDto addRole(@Valid @RequestBody RoleDto roleDto) {

        return roleService.addRole(roleDto);
    }

    @GetMapping("/{roleId}")
    public RoleDto getRoleById(@PathVariable String roleId) {

        return roleService.getRoleById(roleId);
    }
}