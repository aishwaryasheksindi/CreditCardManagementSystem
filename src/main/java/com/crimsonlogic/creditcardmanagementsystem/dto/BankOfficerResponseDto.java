package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class BankOfficerResponseDto extends StaffResponseDto {

    private String branchCode;

    public BankOfficerResponseDto() {
        super();
    }

    public BankOfficerResponseDto(String staffId,
                                  String userId,
                                  String empName,
                                  String empPhone,
                                  LocalDate empDob,
                                  String empAddress,
                                  String empDesignation,
                                  LocalDate empJoiningDate,
                                  String empStatus,
                                  String branchCode) {
        super(staffId, userId, empName, empPhone, empDob, empAddress, empDesignation, empJoiningDate, empStatus);
        this.branchCode = branchCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
