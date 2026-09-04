package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // Register new customer
    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> registerCustomer(
            @jakarta.validation.Valid @RequestBody com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRegistrationRequestDto registrationDto) {

        CustomerResponseDto createdCustomer = customerService.registerCustomer(registrationDto);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdCustomer);
    }

    // Search customers
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDto>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email) {

        List<CustomerResponseDto> customers =
                customerService.searchCustomers(name, phoneNumber, email);

        return ResponseEntity.ok(customers);
    }

    // Get customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @PathVariable String customerId) {

        CustomerResponseDto customerDto =
                customerService.getCustomerById(customerId);

        return ResponseEntity.ok(customerDto);
    }

    // Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerRequestDto customerDto) {

        CustomerResponseDto updatedCustomer =
                customerService.updateCustomer(customerId, customerDto);

        return ResponseEntity.ok(updatedCustomer);
    }
}