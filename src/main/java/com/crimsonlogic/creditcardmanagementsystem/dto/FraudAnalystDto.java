package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class FraudAnalystDto extends StaffDto {

    public FraudAnalystDto() {
        super();
    }

    public FraudAnalystDto(String staffId,
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
