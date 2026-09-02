package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "bank_officers")
public class BankOfficer extends Staff {

    private String branchCode;

    public BankOfficer() {
        super();
    }

    public BankOfficer(String staffId,
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
