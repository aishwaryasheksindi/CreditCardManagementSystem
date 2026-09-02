package com.crimsonlogic.creditcardmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto addUser(UserDto userDto) {

        User user = new User();

        // Generate User ID
        user.setUserId(IdGenerationUtil.generateUserId());

        // Set User details
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        // Find Role using roleId
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(role);

        user.setAccountStatus(userDto.getAccountStatus());

        // Save User
        User savedUser = userRepository.save(user);

        // Convert User Entity to UserDto
        UserDto responseDto = new UserDto();

        responseDto.setUserId(savedUser.getUserId());
        responseDto.setUsername(savedUser.getUsername());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setRoleId(savedUser.getRole().getRoleId());
        responseDto.setAccountStatus(savedUser.getAccountStatus());

        return responseDto;
    }

    @Override
    public UserDto getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto responseDto = new UserDto();

        responseDto.setUserId(user.getUserId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        responseDto.setRoleId(user.getRole().getRoleId());
        responseDto.setAccountStatus(user.getAccountStatus());

        return responseDto;
    }

    @Override
    public UserDto findByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        UserDto responseDto = new UserDto();

        responseDto.setUserId(user.getUserId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        if (user.getRole() != null) {
            responseDto.setRoleId(user.getRole().getRoleId());
            responseDto.setRoleName(user.getRole().getRoleName());
        }
        responseDto.setAccountStatus(user.getAccountStatus());

        return responseDto;
    }
}