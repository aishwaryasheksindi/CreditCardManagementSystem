package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRegistrationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CustomerResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CustomerStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAuditLogService auditLogService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               IAuditLogService auditLogService) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    public CustomerResponseDto getCustomerById(String customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return convertToResponseDto(customer);
    }

    @Override
    public CustomerResponseDto updateCustomer(String customerId, CustomerRequestDto customerDto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

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

        return convertToResponseDto(updatedCustomer);
    }

    @Override
    public List<CustomerResponseDto> searchCustomers(String name, String phoneNumber, String email) {

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
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponseDto registerCustomer(CustomerRegistrationRequestDto registrationDto) {
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists: " + registrationDto.getUsername());
        }

        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()
                || !customerRepository.findByEmail(registrationDto.getEmail()).isEmpty()) {
            throw new DuplicateResourceException("Email already exists: " + registrationDto.getEmail());
        }

        if (!customerRepository.findByPhoneNumber(registrationDto.getPhoneNumber()).isEmpty()) {
            throw new DuplicateResourceException("Phone number already exists: " + registrationDto.getPhoneNumber());
        }

        Role customerRole = roleRepository.findByRoleName("CUSTOMER").orElseGet(() -> {
            Role role = new Role();
            role.setRoleId(IdGenerationUtil.generateRoleId());
            role.setRoleName("CUSTOMER");
            role.setDescription("Customer user");
            return roleRepository.save(role);
        });

        User user = new User();
        user.setUserId(IdGenerationUtil.generateUserId());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(customerRole);
        user.setAccountStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setCustomerId(IdGenerationUtil.generateCustomerId());
        customer.setUserId(savedUser.getUserId());
        customer.setName(registrationDto.getName());
        customer.setEmail(registrationDto.getEmail());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setAddress(registrationDto.getAddress());
        customer.setDateOfBirth(registrationDto.getDateOfBirth());
        customer.setEmployment(registrationDto.getEmployment());
        customer.setIncomeRange(registrationDto.getIncomeRange());
        customer.setKycStatus(KycStatus.PENDING);
        customer.setCustomerStatus(CustomerStatus.ACTIVE);
        Customer savedCustomer = customerRepository.save(customer);

        auditLogService.logAction(savedUser.getUserId(), AuditAction.CREATE, "User", savedUser.getUserId(),
                "New user registered: " + savedUser.getUsername());
        auditLogService.logAction(savedUser.getUserId(), AuditAction.CREATE, "Customer", savedCustomer.getCustomerId(),
                "Customer profile created for user: " + savedUser.getUsername());

        return convertToResponseDto(savedCustomer);
    }

    private CustomerResponseDto convertToResponseDto(Customer customer) {
        CustomerResponseDto customerDto = new CustomerResponseDto();
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
        customerDto.setUserId(customer.getUserId());
        return customerDto;
    }
}