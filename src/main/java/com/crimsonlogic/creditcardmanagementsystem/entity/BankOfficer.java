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
