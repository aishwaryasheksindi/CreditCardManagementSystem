package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class FraudAnalystDto extends StaffDto {

    public FraudAnalystDto() {
        super();
    }

    public FraudAnalystDto(String staffId,
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
