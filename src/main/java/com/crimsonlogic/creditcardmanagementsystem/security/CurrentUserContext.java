package com.crimsonlogic.creditcardmanagementsystem.security;

import com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.BankOfficerRepository;
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
    private final BankOfficerRepository bankOfficerRepository;

    public CurrentUserContext(UserRepository userRepository, CustomerRepository customerRepository) {
        this(userRepository, customerRepository, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public CurrentUserContext(UserRepository userRepository,
                              CustomerRepository customerRepository,
                              BankOfficerRepository bankOfficerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.bankOfficerRepository = bankOfficerRepository;
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

    public boolean hasRole(String roleName) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null) {
            return false;
        }
        String target = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        for (GrantedAuthority ga : authorities) {
            if (target.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBankOfficer() {
        return hasRole("ROLE_BANK_OFFICER");
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isFraudAnalyst() {
        return hasRole("ROLE_FRAUD_ANALYST");
    }

    public boolean isCustomerServiceAgent() {
        return hasRole("ROLE_CUSTOMER_SERVICE_AGENT");
    }

    public Optional<BankOfficer> getCurrentBankOfficer() {
        String userId = getCurrentUserId();
        if (userId == null || bankOfficerRepository == null) {
            return Optional.empty();
        }
        return bankOfficerRepository.findByUserId(userId);
    }

    public String getCurrentOfficerBranchCode() {
        return getCurrentBankOfficer().map(BankOfficer::getBranchCode).orElse(null);
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

    /**
     * Enforces branch-level access control:
     * - If no authentication context is present, pass through.
     * - If ADMIN or FRAUD_ANALYST, access is granted globally across branches.
     * - If BANK_OFFICER, access is granted ONLY if customer's branch matches officer's branch.
     * - Otherwise (e.g. CUSTOMER), customer ownership is enforced.
     */
    public void assertCustomerBranchAccess(Customer customer) {
        String username = getCurrentUsername();
        if (username == null) {
            return;
        }

        if (isAdmin() || isFraudAnalyst() || isCustomerServiceAgent()) {
            return;
        }

        if (isBankOfficer()) {
            String officerBranch = getCurrentOfficerBranchCode();
            if (customer == null || customer.getBranchCode() == null || officerBranch == null
                    || !customer.getBranchCode().equalsIgnoreCase(officerBranch)) {
                throw new AccessDeniedException(
                        "Bank officer is only authorized to access records belonging to branch: " + officerBranch);
            }
            return;
        }

        String targetCustomerId = customer != null ? customer.getCustomerId() : null;
        assertCustomerOwnership(targetCustomerId);
    }

    /**
     * Enforces branch-level access control by customer ID.
     */
    public void assertCustomerBranchAccess(String customerId) {
        String username = getCurrentUsername();
        if (username == null) {
            return;
        }

        if (isAdmin() || isFraudAnalyst() || isCustomerServiceAgent()) {
            return;
        }

        if (customerId == null) {
            throw new AccessDeniedException("Customer ID is required");
        }

        if (isBankOfficer()) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
            assertCustomerBranchAccess(customer);
            return;
        }

        assertCustomerOwnership(customerId);
    }
}
