package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserDto;

public interface IUserService {

    UserDto addUser(UserDto userDto);

    UserDto getUserById(String userId);
}