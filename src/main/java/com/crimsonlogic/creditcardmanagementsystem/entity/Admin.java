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
                 String empName,
                 String empPhone,
                 LocalDate empDob,
                 String empAddress,
                 String empDesignation,
                 LocalDate empJoiningDate,
                 String empStatus) {
        super(staffId, userId, empName, empPhone, empDob, empAddress, empDesignation, empJoiningDate, empStatus);
    }
}
