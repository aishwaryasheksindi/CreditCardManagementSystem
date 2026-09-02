package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "admins")
public class Admin extends Staff {

    public Admin() {
        super();
    }

    public Admin(String staffId,
                 String userId,
                 String name,
                 String phone,
                 LocalDate dob,
                 String address,
                 String designation,
                 LocalDate dateOfJoining,
                 String employeeStatus) {
        super(staffId, userId, name, phone, dob, address, designation, dateOfJoining, employeeStatus);
    }
}
