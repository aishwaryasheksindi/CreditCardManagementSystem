package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class BankOfficerRequestDto extends StaffRequestDto {

    @NotBlank(message = "Branch code is required")
    @Size(max = 50, message = "Branch code must not exceed 50 characters")
    private String branchCode;

    public BankOfficerRequestDto() {
        super();
    }

    public BankOfficerRequestDto(String userId,
                                 String empName,
                                 String empPhone,
                                 LocalDate empDob,
                                 String empAddress,
                                 String empDesignation,
                                 LocalDate empJoiningDate,
                                 String empStatus,
                                 String branchCode) {
        super(userId, empName, empPhone, empDob, empAddress, empDesignation, empJoiningDate, empStatus);
        this.branchCode = branchCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
