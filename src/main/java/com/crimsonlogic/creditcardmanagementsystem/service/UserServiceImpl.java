package com.crimsonlogic.creditcardmanagementsystem.service;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAuditLogService auditLogService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           IAuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {

        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists: " + userRequestDto.getUsername());
        }

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered: " + userRequestDto.getEmail());
        }

        User user = new User();

        // Generate User ID
        user.setUserId(IdGenerationUtil.generateUserId());

        // Set User details
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());

        user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPassword()));

        // Find Role using roleId
        Role role = roleRepository.findById(userRequestDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(role);

        user.setAccountStatus(userRequestDto.getAccountStatus());

        // Save User
        User savedUser = userRepository.save(user);

        auditLogService.logAction(savedUser.getUserId(), AuditAction.CREATE, "User", savedUser.getUserId(), "New user account created: " + savedUser.getUsername());

        return convertToResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToResponseDto(user);
    }

    @Override
    public UserResponseDto findByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return convertToResponseDto(user);
    }

    @Override
    public void recordFailedLoginAttempt(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 3) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            userRepository.save(user);
        });
    }

    @Override
    public void resetFailedLoginAttempts(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
        });
    }

    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(user.getUserId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        if (user.getRole() != null) {
            responseDto.setRoleId(user.getRole().getRoleId());
            responseDto.setRoleName(user.getRole().getRoleName());
        }
        responseDto.setAccountStatus(user.getAccountStatus());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setLastLoginAt(user.getLastLoginAt());
        responseDto.setAccountLockedUntil(user.getAccountLockedUntil());
        return responseDto;
    }
}