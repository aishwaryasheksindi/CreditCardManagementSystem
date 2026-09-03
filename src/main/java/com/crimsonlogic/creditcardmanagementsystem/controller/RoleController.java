package com.crimsonlogic.creditcardmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crimsonlogic.creditcardmanagementsystem.dto.RoleRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.RoleResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IRoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponseDto> addRole(@Valid @RequestBody RoleRequestDto roleRequestDto) {

        return ResponseEntity.ok(roleService.addRole(roleRequestDto));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable String roleId) {

        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }
}