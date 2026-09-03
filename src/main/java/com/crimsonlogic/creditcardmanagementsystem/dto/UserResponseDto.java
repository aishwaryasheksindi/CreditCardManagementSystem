package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDateTime;

public class UserResponseDto {

    private String userId;
    private String username;
    private String email;
    private String roleId;
    private String roleName;
    private String accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public UserResponseDto() {
    }

    public UserResponseDto(String userId, String username, String email, String roleId, String roleName, String accountStatus) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.accountStatus = accountStatus;
    }

    public UserResponseDto(String userId, String username, String email, String roleId, String roleName, String accountStatus, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
