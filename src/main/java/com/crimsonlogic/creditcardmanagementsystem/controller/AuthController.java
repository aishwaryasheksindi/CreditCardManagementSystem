package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.LoginRequest;
import com.crimsonlogic.creditcardmanagementsystem.dto.LoginResponse;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.JwtService;
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
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + request.getUsername()));

        String roleCode = user.getRole() != null && user.getRole().getRoleCode() != null
                ? user.getRole().getRoleCode()
                : "USER";

        String token = jwtService.generateToken(user.getUsername(), roleCode);
        Date expiresAt = jwtService.extractExpiration(token);

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), roleCode, expiresAt));
    }
}

