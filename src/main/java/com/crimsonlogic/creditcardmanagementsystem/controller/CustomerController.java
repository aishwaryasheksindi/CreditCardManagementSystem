package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerDto;
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

    // Search customers
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email) {

        List<CustomerDto> customers =
                customerService.searchCustomers(name, phoneNumber, email);

        return ResponseEntity.ok(customers);
    }

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