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

    private String name;

    private String phone;

    private LocalDate dob;

    private String address;

    private String designation;

    private LocalDate dateOfJoining;

    private String employeeStatus;

    public Staff() {
    }

    public Staff(String staffId,
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
