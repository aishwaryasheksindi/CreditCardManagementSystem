package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class AdminDto extends StaffDto {

    public AdminDto() {
        super();
    }

    public AdminDto(String staffId,
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
