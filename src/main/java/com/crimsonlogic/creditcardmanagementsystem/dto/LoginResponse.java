package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.util.Date;

public class LoginResponse {

    private String token;
    private String username;
    private String roleCode;
    private Date expiresAt;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String roleCode, Date expiresAt) {
        this.token = token;
        this.username = username;
        this.roleCode = roleCode;
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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}

