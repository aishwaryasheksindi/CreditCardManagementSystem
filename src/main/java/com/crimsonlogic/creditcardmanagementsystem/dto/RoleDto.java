package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RoleDto {

    private String roleId;

    @NotBlank(message = "Role name is required")
    @Pattern(
        regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
        message = "Role name must contain only letters and spaces"
    )
    private String roleName;

    @NotBlank(message = "Role code is required")
    @Pattern(
        regexp = "^[A-Z_]+$",
        message = "Role code must contain only uppercase letters and underscores"
    )
    private String roleCode;

    @NotBlank(message = "Role description is required")
    private String description;

    public RoleDto() {
    }

    public RoleDto(String roleId, String roleName, String roleCode, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleCode = roleCode;
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}