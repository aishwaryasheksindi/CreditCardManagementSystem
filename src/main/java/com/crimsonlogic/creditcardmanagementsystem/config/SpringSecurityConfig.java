package com.crimsonlogic.creditcardmanagementsystem.config;

import com.crimsonlogic.creditcardmanagementsystem.security.JwtAuthenticationFilter;
import com.crimsonlogic.creditcardmanagementsystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                                CustomUserDetailsService userDetailsService,
                                PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/api/staff/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cards/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .requestMatchers(HttpMethod.PUT, "/api/cards/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .requestMatchers(HttpMethod.GET, "/api/customers/search").hasAnyRole("ADMIN", "BANK_OFFICER", "CUSTOMER_SERVICE_AGENT")
                        .requestMatchers(HttpMethod.POST, "/api/customers/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .requestMatchers(HttpMethod.POST, "/api/card-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/card-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/disputes/**").hasAnyRole("ADMIN", "CUSTOMER_SERVICE_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/api/disputes/**").hasAnyRole("ADMIN", "CUSTOMER_SERVICE_AGENT")
                        .requestMatchers(HttpMethod.GET, "/api/fraud-alerts/**").hasAnyRole("ADMIN", "FRAUD_ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/api/fraud-alerts/**").hasAnyRole("ADMIN", "FRAUD_ANALYST")
                        .requestMatchers(HttpMethod.POST, "/api/kyc-documents/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .requestMatchers(HttpMethod.PUT, "/api/kyc-documents/**").hasAnyRole("ADMIN", "BANK_OFFICER")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

