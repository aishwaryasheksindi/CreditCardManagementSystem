package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.util.Date;

public class LoginResponse {

    private String token;
    private String username;
    private String roleName;
    private Date expiresAt;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String roleName, Date expiresAt) {
        this.token = token;
        this.username = username;
        this.roleName = roleName;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRole() {
        return roleName;
    }

    public void setRole(String role) {
        this.roleName = role;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}

