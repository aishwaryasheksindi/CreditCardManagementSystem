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
}
