package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class AdminRequestDto extends StaffRequestDto {

    public AdminRequestDto() {
        super();
    }

    public AdminRequestDto(String userId,
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
