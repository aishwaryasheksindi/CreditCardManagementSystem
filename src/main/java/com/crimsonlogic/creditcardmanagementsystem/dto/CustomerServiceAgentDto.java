package com.crimsonlogic.creditcardmanagementsystem.dto;

import java.time.LocalDate;

public class CustomerServiceAgentDto extends StaffDto {

    public CustomerServiceAgentDto() {
        super();
    }

    public CustomerServiceAgentDto(String staffId,
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
