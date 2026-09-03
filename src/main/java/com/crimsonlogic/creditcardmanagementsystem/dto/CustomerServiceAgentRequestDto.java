package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class CustomerServiceAgentRequestDto extends StaffRequestDto {

    public CustomerServiceAgentRequestDto() {
        super();
    }

    public CustomerServiceAgentRequestDto(String userId,
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
