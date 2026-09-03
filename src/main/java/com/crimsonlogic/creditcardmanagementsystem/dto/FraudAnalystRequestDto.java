package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class FraudAnalystRequestDto extends StaffRequestDto {

    public FraudAnalystRequestDto() {
        super();
    }

    public FraudAnalystRequestDto(String userId,
                                  String empName,
                                  String empPhone,
                                  LocalDate empDob,
                                  String empAddress,
                                  String empDesignation,
                                  LocalDate empJoiningDate,
                                  String empStatus) {
        super(userId, empName, empPhone, empDob, empAddress, empDesignation, empJoiningDate, empStatus);
    }
}
