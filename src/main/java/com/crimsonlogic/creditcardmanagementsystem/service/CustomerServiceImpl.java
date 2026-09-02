package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerDto getCustomerById(String customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return convertToDto(customer);
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

        return convertToDto(updatedCustomer);
    }

    @Override
    public List<CustomerDto> searchCustomers(String name, String phoneNumber, String email) {

        List<Customer> results = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            results.addAll(customerRepository.findByNameContainingIgnoreCase(name.trim()));
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            results.addAll(customerRepository.findByPhoneNumber(phoneNumber.trim()));
        }

        if (email != null && !email.isBlank()) {
            results.addAll(customerRepository.findByEmail(email.trim()));
        }

        // If no criteria provided, return all customers
        if ((name == null || name.isBlank())
                && (phoneNumber == null || phoneNumber.isBlank())
                && (email == null || email.isBlank())) {
            results.addAll(customerRepository.findAll());
        }

        // Deduplicate keeping order
        Set<String> seenIds = new LinkedHashSet<>();
        List<Customer> distinctResults = new ArrayList<>();
        for (Customer c : results) {
            if (seenIds.add(c.getCustomerId())) {
                distinctResults.add(c);
            }
        }

        return distinctResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CustomerDto convertToDto(Customer customer) {
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
}