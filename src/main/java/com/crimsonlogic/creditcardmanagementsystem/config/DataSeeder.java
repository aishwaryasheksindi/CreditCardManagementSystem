package com.crimsonlogic.creditcardmanagementsystem.config;

import com.crimsonlogic.creditcardmanagementsystem.entity.Admin;
import com.crimsonlogic.creditcardmanagementsystem.entity.Role;
import com.crimsonlogic.creditcardmanagementsystem.entity.User;
import com.crimsonlogic.creditcardmanagementsystem.repository.AdminRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.RoleRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.UserRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DataSeeder seeds the initial privileged Administrator account if none exists.
 *
 * Seed Admin Credentials:
 * - Username: admin
 * - Password: Admin@1234
 * - Role: ADMIN
 *
 * These credentials can be used to log in via POST /api/auth/login to obtain
 * a JWT and administer the system.
 */
@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedBootstrapAdmin(UserRepository userRepository,
                                               RoleRepository roleRepository,
                                               AdminRepository adminRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                if (!userRepository.existsByRole_RoleName("ADMIN")) {
                    logger.info("No Administrator user found. Creating bootstrap Admin account...");

                    // Ensure ADMIN role exists
                    Role adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleId(IdGenerationUtil.generateRoleId());
                        newRole.setRoleName("ADMIN");
                        newRole.setDescription("System Administrator with full access");
                        return roleRepository.save(newRole);
                    });

                    // Create User row
                    String userId = IdGenerationUtil.generateUserId();
                    User adminUser = new User();
                    adminUser.setUserId(userId);
                    adminUser.setUsername("admin");
                    adminUser.setEmail("admin@ccms.com");
                    adminUser.setPasswordHash(passwordEncoder.encode("Admin@1234"));
                    adminUser.setRole(adminRole);
                    adminUser.setAccountStatus("ACTIVE");
                    adminUser.setCreatedAt(LocalDateTime.now());
                    userRepository.save(adminUser);

                    // Create Admin row
                    String staffId = IdGenerationUtil.generateStaffId();
                    Admin adminStaff = new Admin();
                    adminStaff.setStaffId(staffId);
                    adminStaff.setUserId(userId);
                    adminStaff.setEmpName("System Administrator");
                    adminStaff.setEmpPhone("9999999999");
                    adminStaff.setEmpDob(LocalDate.of(1990, 1, 1));
                    adminStaff.setEmpAddress("Headquarters, IT Dept");
                    adminStaff.setEmpDesignation("System Admin");
                    adminStaff.setEmpJoiningDate(LocalDate.now());
                    adminStaff.setEmpStatus("ACTIVE");
                    adminRepository.save(adminStaff);

                    logger.info("Bootstrap Admin successfully seeded with username 'admin' and staff ID '{}'", staffId);
                } else {
                    logger.info("Administrator user already exists. Skipping bootstrap admin creation.");
                }
            } catch (Exception ex) {
                logger.warn("Bootstrap admin seeding skipped or encountered an issue: {}", ex.getMessage());
            }
        };
    }
}
