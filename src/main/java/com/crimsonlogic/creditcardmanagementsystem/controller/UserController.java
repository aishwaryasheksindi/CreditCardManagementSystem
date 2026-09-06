package com.crimsonlogic.creditcardmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PutMapping;
import com.crimsonlogic.creditcardmanagementsystem.dto.ChangePasswordRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserProfileResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserProfileUpdateRequestDto;

import com.crimsonlogic.creditcardmanagementsystem.dto.UserRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> addUser(
            @Valid @RequestBody UserRequestDto userRequestDto) {

        UserResponseDto savedUser = userService.addUser(userRequestDto);

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDto> updateMyProfile(
            @Valid @RequestBody UserProfileUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateMyProfile(requestDto));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto) {
        userService.changeMyPassword(requestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable String userId) {

        UserResponseDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(userDto);
    }
}