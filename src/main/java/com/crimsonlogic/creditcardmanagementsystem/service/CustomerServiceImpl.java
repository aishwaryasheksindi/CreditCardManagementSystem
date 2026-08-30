package com.crimsonlogic.creditcardmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerDto getCustomerById(String customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerDto customerDto = new CustomerDto();

        customerDto.setCustomerId(customer.getCustomerId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setAddress(customer.getAddress());
        customerDto.setDateOfBirth(customer.getDateOfBirth());
        customerDto.setEmployment(customer.getEmployment());
        customerDto.setIncomeRange(customer.getIncomeRange());
        customerDto.setKycStatus(customer.getKycStatus());
        customerDto.setCreditProfile(customer.getCreditProfile());
        customerDto.setCustomerStatus(customer.getCustomerStatus());

        return customerDto;
    }

    @Override
    public CustomerDto updateCustomer(String customerId, CustomerDto customerDto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setAddress(customerDto.getAddress());
        customer.setDateOfBirth(customerDto.getDateOfBirth());
        customer.setEmployment(customerDto.getEmployment());
        customer.setIncomeRange(customerDto.getIncomeRange());
        customer.setKycStatus(customerDto.getKycStatus());
        customer.setCreditProfile(customerDto.getCreditProfile());
        customer.setCustomerStatus(customerDto.getCustomerStatus());

        Customer updatedCustomer = customerRepository.save(customer);

        CustomerDto responseDto = new CustomerDto();

        responseDto.setCustomerId(updatedCustomer.getCustomerId());
        responseDto.setName(updatedCustomer.getName());
        responseDto.setEmail(updatedCustomer.getEmail());
        responseDto.setPhoneNumber(updatedCustomer.getPhoneNumber());
        responseDto.setAddress(updatedCustomer.getAddress());
        responseDto.setDateOfBirth(updatedCustomer.getDateOfBirth());
        responseDto.setEmployment(updatedCustomer.getEmployment());
        responseDto.setIncomeRange(updatedCustomer.getIncomeRange());
        responseDto.setKycStatus(updatedCustomer.getKycStatus());
        responseDto.setCreditProfile(updatedCustomer.getCreditProfile());
        responseDto.setCustomerStatus(updatedCustomer.getCustomerStatus());

        return responseDto;
    }
}