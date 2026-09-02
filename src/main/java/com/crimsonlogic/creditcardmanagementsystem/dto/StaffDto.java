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
    private String name;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @NotNull(message = "Date of joining is required")
    private LocalDate dateOfJoining;

    @NotBlank(message = "Employee status is required")
    @Size(max = 50, message = "Employee status must not exceed 50 characters")
    private String employeeStatus;

    public StaffDto() {
    }

    public StaffDto(String staffId,
                    String userId,
                    String name,
                    String phone,
                    LocalDate dob,
                    String address,
                    String designation,
                    LocalDate dateOfJoining,
                    String employeeStatus) {
        this.staffId = staffId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.designation = designation;
        this.dateOfJoining = dateOfJoining;
        this.employeeStatus = employeeStatus;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }
}
