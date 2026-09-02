package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RoleDto {

    private String roleId;

    @NotBlank(message = "Role name is required")
    @Pattern(
        regexp = "^[A-Z_]+$",
        message = "Role name must contain only uppercase letters and underscores"
    )
    private String roleName;

    @NotBlank(message = "Role description is required")
    private String description;

    public RoleDto() {
    }

    public RoleDto(String roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}