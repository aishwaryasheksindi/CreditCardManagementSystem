package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.service.CustomUserDetailsService;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import com.crimsonlogic.creditcardmanagementsystem.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAuditLogService auditLogService;

    @InjectMocks
    private UserServiceImpl userService;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testRecordFailedLoginAttempt_UnderThreeAttempts_DoesNotLock() {
        User user = new User();
        user.setUserId("USR001");
        user.setUsername("testuser");
        user.setFailedLoginAttempts(0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.recordFailedLoginAttempt("testuser");

        assertEquals(1, user.getFailedLoginAttempts());
        assertNull(user.getAccountLockedUntil());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRecordFailedLoginAttempt_ReachesThreeAttempts_LocksFor15Minutes() {
        User user = new User();
        user.setUserId("USR001");
        user.setUsername("testuser");
        user.setFailedLoginAttempts(2);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.recordFailedLoginAttempt("testuser");

        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getAccountLockedUntil());
        assertTrue(user.getAccountLockedUntil().isAfter(LocalDateTime.now().plusMinutes(14)));
        assertTrue(user.getAccountLockedUntil().isBefore(LocalDateTime.now().plusMinutes(16)));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testResetFailedLoginAttempts_ResetsAttemptsAndLockTimestamp() {
        User user = new User();
        user.setUserId("USR001");
        user.setUsername("testuser");
        user.setFailedLoginAttempts(3);
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));

        when(userRepository.findById("USR001")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.resetFailedLoginAttempts("USR001");

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getAccountLockedUntil());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCustomUserDetailsService_WhenAccountLocked_AccountNonLockedIsFalse() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashedpass");
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        user.setRole(role);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void testCustomUserDetailsService_WhenAccountNotLocked_AccountNonLockedIsTrue() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashedpass");
        user.setAccountLockedUntil(null);
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        user.setRole(role);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testCustomUserDetailsService_WhenLockExpired_AccountNonLockedIsTrue() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashedpass");
        user.setAccountLockedUntil(LocalDateTime.now().minusMinutes(5));
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        user.setRole(role);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertTrue(userDetails.isAccountNonLocked());
    }
}
