package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.entity.*;
import com.crimsonlogic.creditcardmanagementsystem.repository.*;
import com.crimsonlogic.creditcardmanagementsystem.service.StaffServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BankOfficerRepository bankOfficerRepository;

    @Mock
    private FraudAnalystRepository fraudAnalystRepository;

    @Mock
    private CustomerServiceAgentRepository customerServiceAgentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StaffServiceImpl staffService;

    @Test
    void testAddAdmin_Success() {
        AdminDto adminDto = new AdminDto();
        adminDto.setUserId("USR1001");
        adminDto.setName("System Admin");
        adminDto.setPhone("9876543210");
        adminDto.setDob(LocalDate.of(1990, 1, 1));
        adminDto.setAddress("123 Admin St");
        adminDto.setDesignation("Chief Administrator");
        adminDto.setDateOfJoining(LocalDate.of(2020, 1, 1));
        adminDto.setEmployeeStatus("ACTIVE");

        when(userRepository.existsById("USR1001")).thenReturn(true);
        when(staffRepository.existsById(any())).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminDto result = staffService.addAdmin(adminDto);

        assertNotNull(result);
        assertNotNull(result.getStaffId());
        assertTrue(result.getStaffId().startsWith("STF"));
        assertEquals("System Admin", result.getName());
    }

    @Test
    void testAddBankOfficer_SuccessWithBranchCode() {
        BankOfficerDto officerDto = new BankOfficerDto();
        officerDto.setUserId("USR1002");
        officerDto.setName("John Officer");
        officerDto.setPhone("9876543211");
        officerDto.setDob(LocalDate.of(1992, 5, 10));
        officerDto.setAddress("456 Bank Blvd");
        officerDto.setDesignation("Senior Branch Officer");
        officerDto.setDateOfJoining(LocalDate.of(2021, 3, 15));
        officerDto.setEmployeeStatus("ACTIVE");
        officerDto.setBranchCode("BR-MUMBAI-01");

        when(userRepository.existsById("USR1002")).thenReturn(true);
        when(staffRepository.existsById(any())).thenReturn(false);
        when(bankOfficerRepository.save(any(BankOfficer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankOfficerDto result = staffService.addBankOfficer(officerDto);

        assertNotNull(result);
        assertNotNull(result.getStaffId());
        assertEquals("BR-MUMBAI-01", result.getBranchCode());
    }
}
