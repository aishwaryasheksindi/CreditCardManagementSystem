package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "fraud_analysts")
public class FraudAnalyst extends Staff {

    public FraudAnalyst() {
        super();
    }

    public FraudAnalyst(String staffId,
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
