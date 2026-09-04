package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.controller.AuthController;
import com.crimsonlogic.creditcardmanagementsystem.dto.LoginRequest;
import com.crimsonlogic.creditcardmanagementsystem.dto.UserResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.exception.GlobalExceptionHandler;
import com.crimsonlogic.creditcardmanagementsystem.security.JwtService;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import com.crimsonlogic.creditcardmanagementsystem.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private IUserService userService;

    @Mock
    private IAuditLogService auditLogService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testLogin_BadCredentials_Returns401AndRecordsFailedAttempt() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        verify(userService, times(1)).recordFailedLoginAttempt("alice");
        verify(userService, never()).resetFailedLoginAttempts(any());
    }

    @Test
    void testLogin_AccountLocked_Returns423Locked() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("AnyPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new LockedException("Account is locked"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(423))
                .andExpect(jsonPath("$.status").value(423))
                .andExpect(jsonPath("$.error").value("Locked"))
                .andExpect(jsonPath("$.message").value("Account temporarily locked due to multiple failed login attempts. Please try again later."));

        verify(userService, times(1)).recordFailedLoginAttempt("alice");
    }

    @Test
    void testLogin_Success_ResetsFailedAttempts() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("CorrectPassword");

        UserResponseDto userDto = new UserResponseDto();
        userDto.setUserId("USR100");
        userDto.setUsername("alice");
        userDto.setRoleName("CUSTOMER");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userService.findByUsername("alice")).thenReturn(userDto);
        when(jwtService.generateToken("alice", "CUSTOMER")).thenReturn("mock-jwt-token");
        when(jwtService.extractExpiration("mock-jwt-token")).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("alice"));

        verify(userService, times(1)).resetFailedLoginAttempts("USR100");
    }
}
