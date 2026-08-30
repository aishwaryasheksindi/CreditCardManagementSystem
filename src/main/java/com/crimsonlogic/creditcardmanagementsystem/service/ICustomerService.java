package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;

public interface ICustomerService {

    CustomerDto getCustomerById(String customerId);

    CustomerDto updateCustomer(String customerId, CustomerDto customerDto);
}