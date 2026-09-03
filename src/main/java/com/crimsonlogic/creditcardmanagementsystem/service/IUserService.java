package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;

public interface IUserService {

    UserResponseDto addUser(UserRequestDto userRequestDto);

    UserResponseDto getUserById(String userId);

    UserResponseDto findByUsername(String username);
}