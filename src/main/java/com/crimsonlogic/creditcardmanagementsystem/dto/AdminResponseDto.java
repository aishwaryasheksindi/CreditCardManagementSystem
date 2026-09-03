package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class AdminResponseDto extends StaffResponseDto {

    public AdminResponseDto() {
        super();
    }

    public AdminResponseDto(String staffId,
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
