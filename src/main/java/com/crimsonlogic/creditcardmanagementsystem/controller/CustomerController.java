package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;
import com.crimsonlogic.creditcardmanagementsystem.service.ICustomerService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    // Get customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomerById(
            @PathVariable String customerId) {

        CustomerDto customerDto =
                customerService.getCustomerById(customerId);

        return ResponseEntity.ok(customerDto);
    }

    // Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerDto customerDto) {

        CustomerDto updatedCustomer =
                customerService.updateCustomer(customerId, customerDto);

        return ResponseEntity.ok(updatedCustomer);
    }
}