package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerResponseDto;

import java.util.List;

public interface ICustomerService {

    CustomerResponseDto getCustomerById(String customerId);

    CustomerResponseDto updateCustomer(String customerId, CustomerRequestDto customerDto);

    List<CustomerResponseDto> searchCustomers(String name, String phoneNumber, String email);
}