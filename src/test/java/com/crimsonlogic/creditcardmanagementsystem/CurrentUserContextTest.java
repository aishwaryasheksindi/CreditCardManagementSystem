package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserContextTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private com.crimsonlogic.creditcardmanagementsystem.repository.BankOfficerRepository bankOfficerRepository;

    @InjectMocks
    private CurrentUserContext currentUserContext;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAssertCustomerOwnership_StaffRole_Permitted() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Should not throw even when targetCustomerId is any customer
        assertDoesNotThrow(() -> currentUserContext.assertCustomerOwnership("CUST9999"));
    }

    @Test
    void testAssertCustomerOwnership_CustomerRole_MatchingCustomerId_Permitted() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "john_user", "password", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUserId("USR1001");
        user.setUsername("john_user");

        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setUserId("USR1001");

        when(userRepository.findByUsername("john_user")).thenReturn(Optional.of(user));
        when(customerRepository.findByUserId("USR1001")).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> currentUserContext.assertCustomerOwnership("CUST1001"));
    }

    @Test
    void testAssertCustomerOwnership_CustomerRole_DifferentCustomerId_ThrowsAccessDenied() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "john_user", "password", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUserId("USR1001");
        user.setUsername("john_user");

        Customer customer = new Customer();
        customer.setCustomerId("CUST1001");
        customer.setUserId("USR1001");

        when(userRepository.findByUsername("john_user")).thenReturn(Optional.of(user));
        when(customerRepository.findByUserId("USR1001")).thenReturn(Optional.of(customer));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            currentUserContext.assertCustomerOwnership("CUST2002");
        });

        assertEquals("You are not authorized to access this resource", ex.getMessage());
    }

    @Test
    void testAssertCustomerOwnership_NoAuthentication_PermitsAccess() {
        SecurityContextHolder.clearContext();
        assertDoesNotThrow(() -> currentUserContext.assertCustomerOwnership("CUST1001"));
    }

    @Test
    void testAssertCustomerBranchAccess_AdminRole_PermittedGlobally() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "ananya_admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Customer cust1 = new Customer();
        cust1.setCustomerId("CUST1001");
        cust1.setBranchCode("CN8080");

        Customer cust2 = new Customer();
        cust2.setCustomerId("CUST1002");
        cust2.setBranchCode("CN4200");

        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust1));
        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust2));
    }

    @Test
    void testAssertCustomerBranchAccess_FraudAnalystRole_PermittedGlobally() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "vikram_fraud", "password", List.of(new SimpleGrantedAuthority("ROLE_FRAUD_ANALYST")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Customer cust1 = new Customer();
        cust1.setCustomerId("CUST1001");
        cust1.setBranchCode("CN8080");

        Customer cust2 = new Customer();
        cust2.setCustomerId("CUST1002");
        cust2.setBranchCode("CN4200");

        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust1));
        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust2));
    }

    @Test
    void testAssertCustomerBranchAccess_BankOfficer_SameBranch_Permitted() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "priya_officer", "password", List.of(new SimpleGrantedAuthority("ROLE_BANK_OFFICER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUserId("USR_BO_1");
        user.setUsername("priya_officer");
        when(userRepository.findByUsername("priya_officer")).thenReturn(Optional.of(user));

        com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer bo =
                new com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer();
        bo.setUserId("USR_BO_1");
        bo.setBranchCode("CN8080");
        when(bankOfficerRepository.findByUserId("USR_BO_1")).thenReturn(Optional.of(bo));

        Customer cust = new Customer();
        cust.setCustomerId("CUST1001");
        cust.setBranchCode("CN8080");

        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust));
    }

    @Test
    void testAssertCustomerBranchAccess_BankOfficer_DifferentBranch_ThrowsAccessDenied() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "priya_officer", "password", List.of(new SimpleGrantedAuthority("ROLE_BANK_OFFICER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUserId("USR_BO_1");
        user.setUsername("priya_officer");
        when(userRepository.findByUsername("priya_officer")).thenReturn(Optional.of(user));

        com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer bo =
                new com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer();
        bo.setUserId("USR_BO_1");
        bo.setBranchCode("CN8080");
        when(bankOfficerRepository.findByUserId("USR_BO_1")).thenReturn(Optional.of(bo));

        Customer cust = new Customer();
        cust.setCustomerId("CUST1002");
        cust.setBranchCode("CN4200");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                currentUserContext.assertCustomerBranchAccess(cust)
        );
        assertTrue(ex.getMessage().contains("CN8080"));
    }

    @Test
    void testAssertCustomerBranchAccessById_BankOfficer_SameBranch_Permitted() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "priya_officer", "password", List.of(new SimpleGrantedAuthority("ROLE_BANK_OFFICER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUserId("USR_BO_1");
        user.setUsername("priya_officer");
        when(userRepository.findByUsername("priya_officer")).thenReturn(Optional.of(user));

        com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer bo =
                new com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer();
        bo.setUserId("USR_BO_1");
        bo.setBranchCode("CN8080");
        when(bankOfficerRepository.findByUserId("USR_BO_1")).thenReturn(Optional.of(bo));

        Customer cust = new Customer();
        cust.setCustomerId("CUST1001");
        cust.setBranchCode("CN8080");
        when(customerRepository.findById("CUST1001")).thenReturn(Optional.of(cust));

        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess("CUST1001"));
    }

    @Test
    void testAssertCustomerBranchAccess_CustomerServiceAgentRole_PermittedGlobally() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "csa_agent", "password", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER_SERVICE_AGENT")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(currentUserContext.isCustomerServiceAgent());

        Customer cust1 = new Customer();
        cust1.setCustomerId("CUST1001");
        cust1.setBranchCode("CN8080");

        Customer cust2 = new Customer();
        cust2.setCustomerId("CUST1002");
        cust2.setBranchCode("CN4200");

        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust1));
        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess(cust2));
        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess("CUST1001"));
        assertDoesNotThrow(() -> currentUserContext.assertCustomerBranchAccess("CUST1002"));
    }
}
