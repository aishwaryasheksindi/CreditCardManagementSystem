package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class BankOfficerDto extends StaffDto {

    @NotBlank(message = "Branch code is required")
    @Size(max = 50, message = "Branch code must not exceed 50 characters")
    private String branchCode;

    public BankOfficerDto() {
        super();
    }

    public BankOfficerDto(String staffId,
                          String userId,
                          String name,
                          String phone,
                          LocalDate dob,
                          String address,
                          String designation,
                          LocalDate dateOfJoining,
                          String employeeStatus,
                          String branchCode) {
        super(staffId, userId, name, phone, dob, address, designation, dateOfJoining, employeeStatus);
        this.branchCode = branchCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
