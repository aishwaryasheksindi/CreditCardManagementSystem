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
import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CustomerStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

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

    @Mock
    private com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext currentUserContext;

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
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", "CN8080"
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
        assertEquals("CN8080", response.getBranchCode());
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

    @Test
    void testGetMyCustomerProfile_Success() {
        String userId = "USER1001";
        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setName("John Doe");
        customer.setUserId(userId);

        when(currentUserContext.getCurrentUserId()).thenReturn(userId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));

        CustomerResponseDto result = customerService.getMyCustomerProfile();

        assertNotNull(result);
        assertEquals("CUST1001", result.getCustomerId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testRegisterCustomer_WithCN8080_Success() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "rahul8080", "rahul8080@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "Koramangala, Bangalore",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", "CN8080"
        );

        when(userRepository.findByUsername("rahul8080")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("rahul8080@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("rahul8080@example.com")).thenReturn(Collections.emptyList());
        when(customerRepository.findByPhoneNumber("9876543210")).thenReturn(Collections.emptyList());

        Role customerRole = new Role("ROLE0002", "CUSTOMER", "Customer role");
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword123");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponseDto response = customerService.registerCustomer(request);

        assertNotNull(response);
        assertEquals("CN8080", response.getBranchCode());
    }

    @Test
    void testRegisterCustomer_WithCN4200_Success() {
        CustomerRegistrationRequestDto request = new CustomerRegistrationRequestDto(
                "priya4200", "priya4200@example.com", "Password@123",
                "Priya Nair", "9876543211", "Indiranagar, Bangalore",
                LocalDate.of(1996, 6, 20), "Salaried", "500000-1000000", "CN4200"
        );

        when(userRepository.findByUsername("priya4200")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("priya4200@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("priya4200@example.com")).thenReturn(Collections.emptyList());
        when(customerRepository.findByPhoneNumber("9876543211")).thenReturn(Collections.emptyList());

        Role customerRole = new Role("ROLE0002", "CUSTOMER", "Customer role");
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword123");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponseDto response = customerService.registerCustomer(request);

        assertNotNull(response);
        assertEquals("CN4200", response.getBranchCode());
    }

    @Test
    void testBranchCodeValidation_ValidAndInvalid() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        CustomerRegistrationRequestDto validDto1 = new CustomerRegistrationRequestDto(
                "rahul123", "rahul@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", "CN8080"
        );
        assertTrue(validator.validateProperty(validDto1, "branchCode").isEmpty());

        CustomerRegistrationRequestDto validDto2 = new CustomerRegistrationRequestDto(
                "rahul123", "rahul@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", "CN4200"
        );
        assertTrue(validator.validateProperty(validDto2, "branchCode").isEmpty());

        CustomerRegistrationRequestDto invalidDto = new CustomerRegistrationRequestDto(
                "rahul123", "rahul@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", "CN9999"
        );
        Set<ConstraintViolation<CustomerRegistrationRequestDto>> violations = validator.validateProperty(invalidDto, "branchCode");
        assertFalse(violations.isEmpty());
        assertEquals("Branch code must be either CN8080 or CN4200", violations.iterator().next().getMessage());

        CustomerRegistrationRequestDto blankDto = new CustomerRegistrationRequestDto(
                "rahul123", "rahul@example.com", "Password@123",
                "Rahul Sharma", "9876543210", "123 MG Road",
                LocalDate.of(1995, 5, 15), "Salaried", "500000-1000000", ""
        );
        assertFalse(validator.validateProperty(blankDto, "branchCode").isEmpty());
    }

    @Test
    void testCustomerRequestDto_DoesNotAcceptBranchCode() {
        // Scenario 4: Branch code is not accepted through normal Customer Update Profile
        boolean hasBranchCodeField = false;
        for (java.lang.reflect.Field field : CustomerRequestDto.class.getDeclaredFields()) {
            if ("branchCode".equalsIgnoreCase(field.getName())) {
                hasBranchCodeField = true;
                break;
            }
        }
        assertFalse(hasBranchCodeField, "CustomerRequestDto must not contain branchCode field");
    }

    @Test
    void testUpdateCustomer_CustomerBranchRemainsUnchanged() {
        // Scenario 5: Customer branch remains unchanged through normal profile update
        String customerId = "CUST1001";
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(customerId);
        existingCustomer.setName("Original Name");
        existingCustomer.setEmail("original@example.com");
        existingCustomer.setPhoneNumber("9876543210");
        existingCustomer.setAddress("Original Address");
        existingCustomer.setEmployment("Salaried");
        existingCustomer.setIncomeRange("500000-1000000");
        existingCustomer.setBranchCode("CN8080");

        CustomerRequestDto updateDto = new CustomerRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhoneNumber("9876543210");
        updateDto.setAddress("New Address");
        updateDto.setEmployment("Self-Employed");
        updateDto.setIncomeRange("1000000+");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponseDto response = customerService.updateCustomer(customerId, updateDto);

        assertNotNull(response);
        assertEquals("Updated Name", response.getName());
        assertEquals("CN8080", response.getBranchCode());
        assertEquals("CN8080", existingCustomer.getBranchCode());
        verify(customerRepository).save(existingCustomer);
    }
}
