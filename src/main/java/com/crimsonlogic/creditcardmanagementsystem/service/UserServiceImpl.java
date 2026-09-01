package com.crimsonlogic.creditcardmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto addUser(UserDto userDto) {

        User user = new User();

        // Generate User ID
        user.setUserId(IdGenerationUtil.generateUserId());

        // Set User details
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        // For now
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        // Find Role using roleId
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto responseDto = new UserDto();

        responseDto.setUserId(user.getUserId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        responseDto.setRoleId(user.getRole().getRoleId());
        responseDto.setAccountStatus(user.getAccountStatus());

        return responseDto;
    }
}