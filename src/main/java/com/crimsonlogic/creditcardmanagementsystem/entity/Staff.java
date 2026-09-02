package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Staff {

    @Id
    private String staffId;

    private String userId;

    private String empName;

    private String empPhone;

    private LocalDate empDob;

    private String empAddress;

    private String empDesignation;

    private LocalDate empJoiningDate;

    private String empStatus;

    public Staff() {
    }

    public Staff(String staffId,
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
