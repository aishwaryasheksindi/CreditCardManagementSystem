package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class FraudAnalystResponseDto extends StaffResponseDto {

    public FraudAnalystResponseDto() {
        super();
    }

    public FraudAnalystResponseDto(String staffId,
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
