package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class StaffDto {

    private String staffId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String empName;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String empPhone;

    @NotNull(message = "Date of birth is required")
    private LocalDate empDob;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String empAddress;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String empDesignation;

    @NotNull(message = "Date of joining is required")
    private LocalDate empJoiningDate;

    @NotBlank(message = "Employee status is required")
    @Size(max = 50, message = "Employee status must not exceed 50 characters")
    private String empStatus;

    public StaffDto() {
    }

    public StaffDto(String staffId,
                    String userId,
                    String empName,
                    String empPhone,
                    LocalDate empDob,
                    String empAddress,
                    String empDesignation,
                    LocalDate empJoiningDate,
                    String empStatus) {
        this.staffId = staffId;
        this.userId = userId;
        this.empName = empName;
        this.empPhone = empPhone;
        this.empDob = empDob;
        this.empAddress = empAddress;
        this.empDesignation = empDesignation;
        this.empJoiningDate = empJoiningDate;
        this.empStatus = empStatus;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpPhone() {
        return empPhone;
    }

    public void setEmpPhone(String empPhone) {
        this.empPhone = empPhone;
    }

    public LocalDate getEmpDob() {
        return empDob;
    }

    public void setEmpDob(LocalDate empDob) {
        this.empDob = empDob;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }

    public String getEmpDesignation() {
        return empDesignation;
    }

    public void setEmpDesignation(String empDesignation) {
        this.empDesignation = empDesignation;
    }

    public LocalDate getEmpJoiningDate() {
        return empJoiningDate;
    }

    public void setEmpJoiningDate(LocalDate empJoiningDate) {
        this.empJoiningDate = empJoiningDate;
    }

    public String getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(String empStatus) {
        this.empStatus = empStatus;
    }
}
