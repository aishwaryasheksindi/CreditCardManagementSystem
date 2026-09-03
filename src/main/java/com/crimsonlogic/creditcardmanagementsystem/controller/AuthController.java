package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.LoginRequest;
import com.crimsonlogic.creditcardmanagementsystem.dto.LoginResponse;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.security.JwtService;
import com.crimsonlogic.creditcardmanagementsystem.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserResponseDto user = userService.findByUsername(request.getUsername());

        String roleName = user.getRoleName() != null
                ? user.getRoleName()
                : "USER";

        String token = jwtService.generateToken(user.getUsername(), roleName);
        Date expiresAt = jwtService.extractExpiration(token);

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), roleName, expiresAt));
    }
}

