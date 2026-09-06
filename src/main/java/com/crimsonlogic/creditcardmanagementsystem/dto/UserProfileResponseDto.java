package com.crimsonlogic.creditcardmanagementsystem.dto;

public class UserProfileResponseDto {
    private String userId;
    private String username;
    private String email;
    private String roleName;
    private String actualName;
    private String phoneNumber;
    private String accountType;

    public UserProfileResponseDto() {}

    public UserProfileResponseDto(String userId, String username, String email, String roleName,
                                  String actualName, String phoneNumber, String accountType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.actualName = actualName;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getActualName() { return actualName; }
    public void setActualName(String actualName) { this.actualName = actualName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
