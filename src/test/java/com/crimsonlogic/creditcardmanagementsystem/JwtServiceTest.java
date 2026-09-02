package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        String username = "admin_user";
        String roleCode = "ADMIN";

        String token = jwtService.generateToken(username, roleCode);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(username, jwtService.extractUsername(token));
        assertEquals(roleCode, jwtService.extractRoleCode(token));
        assertFalse(jwtService.isTokenExpired(token));

        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
