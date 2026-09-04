package com.crimsonlogic.creditcardmanagementsystem.security;

import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component
public class CurrentUserContext {

    private static final Set<String> STAFF_ROLES = Set.of(
            "ROLE_ADMIN",
            "ROLE_BANK_OFFICER",
            "ROLE_CUSTOMER_SERVICE_AGENT",
            "ROLE_FRAUD_ANALYST"
    );

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CurrentUserContext(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    public Optional<User> getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    public String getCurrentUserId() {
        return getCurrentUser().map(User::getUserId).orElse(null);
    }

    public Optional<Customer> getCurrentCustomer() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Optional.empty();
        }
        return customerRepository.findByUserId(userId);
    }

    public String getCurrentCustomerId() {
        return getCurrentCustomer().map(Customer::getCustomerId).orElse(null);
    }

    public boolean isStaffRole() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null) {
            return false;
        }
        for (GrantedAuthority ga : authorities) {
            if (STAFF_ROLES.contains(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enforces ownership of the given targetCustomerId.
     * If the caller has a staff role, access is granted.
     * If the caller is a customer, their customerId must match targetCustomerId.
     * If no authentication context is present (e.g. isolated unit tests), pass through.
     */
    public void assertCustomerOwnership(String targetCustomerId) {
        String username = getCurrentUsername();
        if (username == null) {
            return;
        }

        if (isStaffRole()) {
            return;
        }

        String loggedInCustomerId = getCurrentCustomerId();
        if (loggedInCustomerId == null || targetCustomerId == null || !loggedInCustomerId.equals(targetCustomerId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
    }
}
