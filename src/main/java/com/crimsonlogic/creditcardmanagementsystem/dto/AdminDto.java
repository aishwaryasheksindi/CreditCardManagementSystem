package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class AdminDto extends StaffDto {

    public AdminDto() {
        super();
    }

    public AdminDto(String staffId,
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
