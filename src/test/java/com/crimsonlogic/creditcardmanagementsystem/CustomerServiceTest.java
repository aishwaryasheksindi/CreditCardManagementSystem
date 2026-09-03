package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testSearchCustomers_ByName() {
        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhoneNumber("9876543210");

        when(customerRepository.findByNameContainingIgnoreCase("John")).thenReturn(List.of(customer));

        List<CustomerResponseDto> results = customerService.searchCustomers("John", null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }

    @Test
    void testSearchCustomers_ByPhone() {
        Customer customer = new Customer();
        customer.setCustomerId("CUST1002");
        customer.setName("Jane Smith");
        customer.setEmail("jane@example.com");
        customer.setPhoneNumber("9998887776");

        when(customerRepository.findByPhoneNumber("9998887776")).thenReturn(List.of(customer));

        List<CustomerResponseDto> results = customerService.searchCustomers(null, "9998887776", null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Jane Smith", results.get(0).getName());
    }
}
