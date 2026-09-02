package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;

import java.util.List;

public interface ICustomerService {

    CustomerDto getCustomerById(String customerId);

    CustomerDto updateCustomer(String customerId, CustomerDto customerDto);

    List<CustomerDto> searchCustomers(String name, String phoneNumber, String email);
}