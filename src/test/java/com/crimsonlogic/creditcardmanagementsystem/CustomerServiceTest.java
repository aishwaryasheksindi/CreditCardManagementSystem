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

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRegistrationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CustomerStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAuditLogService auditLogService;

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

    @Test
    void testRegisterCustomer_Success() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "rahul123", "rahul@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road, Bangalore",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000"
        );

        when(userRepository.findByUsername("rahul123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("rahul@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("rahul@example.com")).thenReturn(Collections.emptyList());
        when(customerRepository.findByPhoneNumber("9876543210")).thenReturn(Collections.emptyList());

        Role customerRole = new Role("ROLE0002", "CUSTOMER", "Customer role");
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword123");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u;
        });

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            return c;
        });

        CustomerResponseDto response = customerService.registerCustomer(request);

        assertNotNull(response);
        assertEquals("Rahul Sharma", response.getName());
        assertEquals("rahul@example.com", response.getEmail());
        assertEquals("9876543210", response.getPhoneNumber());
        assertEquals(KycStatus.PENDING, response.getKycStatus());
        assertEquals(CustomerStatus.ACTIVE, response.getCustomerStatus());
        assertNotNull(response.getUserId());
        assertNotNull(response.getCustomerId());

        verify(userRepository).save(any(User.class));
        verify(customerRepository).save(any(Customer.class));
        verify(auditLogService, times(2)).logAction(anyString(), eq(AuditAction.CREATE), anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterCustomer_DuplicateUsername() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "existingUser", "email@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000"
        );

        User existing = new User();
        existing.setUsername("existingUser");
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> customerService.registerCustomer(request));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testRegisterCustomer_DuplicateEmail() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "newUser", "existing@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000"
        );

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> customerService.registerCustomer(request));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testRegisterCustomer_DuplicatePhoneNumber() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "newUser", "new@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000"
        );

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("new@example.com")).thenReturn(Collections.emptyList());
        when(customerRepository.findByPhoneNumber("9876543210")).thenReturn(List.of(new Customer()));

        assertThrows(DuplicateResourceException.class, () -> customerService.registerCustomer(request));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
