package com.crimsonlogic.creditcardmanagementsystem.dto;

public class UserProfileResponseDto {
    private String userId;
    private String username;
    private String email;
    private String roleName;
    private String actualName;
    private String phoneNumber;
    private String accountType;
    private String staffId;
    private String branchCode;
    private String branchName;
    private String branchAddress;
    private String accountStatus;

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

    public UserProfileResponseDto(String userId, String username, String email, String roleName,
                                  String actualName, String phoneNumber, String accountType,
                                  String staffId, String branchCode, String branchName,
                                  String branchAddress, String accountStatus) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.actualName = actualName;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
        this.staffId = staffId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.accountStatus = accountStatus;
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

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchAddress() { return branchAddress; }
    public void setBranchAddress(String branchAddress) { this.branchAddress = branchAddress; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
}
