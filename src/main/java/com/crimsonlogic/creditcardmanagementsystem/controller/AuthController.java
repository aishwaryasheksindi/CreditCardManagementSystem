package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.LoginRequest;
import com.crimsonlogic.creditcardmanagementsystem.dto.LoginResponse;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.security.JwtService;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import com.crimsonlogic.creditcardmanagementsystem.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;
    private final IAuditLogService auditLogService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IUserService userService,
                          IAuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        try {
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

            userService.resetFailedLoginAttempts(user.getUserId());

            auditLogService.logAction(user.getUserId(), AuditAction.LOGIN, "User", user.getUserId(), "User logged in");

            return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), roleName, expiresAt));
        } catch (AuthenticationException ex) {
            userService.recordFailedLoginAttempt(request.getUsername());
            auditLogService.logAction(null, AuditAction.LOGIN_FAILED, "User", null, "Failed login attempt for username: " + request.getUsername());
            throw ex;
        }
    }
}
