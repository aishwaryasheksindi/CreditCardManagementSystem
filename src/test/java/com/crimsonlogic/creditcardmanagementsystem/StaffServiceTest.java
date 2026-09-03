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
        AdminRequestDto adminDto = new AdminRequestDto();
        adminDto.setUserId("USR1001");
        adminDto.setEmpName("System Admin");
        adminDto.setEmpPhone("9876543210");
        adminDto.setEmpDob(LocalDate.of(1990, 1, 1));
        adminDto.setEmpAddress("123 Admin St");
        adminDto.setEmpDesignation("Chief Administrator");
        adminDto.setEmpJoiningDate(LocalDate.of(2020, 1, 1));
        adminDto.setEmpStatus("ACTIVE");

        when(userRepository.existsById("USR1001")).thenReturn(true);
        when(staffRepository.existsById(any())).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminResponseDto result = staffService.addAdmin(adminDto);

        assertNotNull(result);
        assertNotNull(result.getStaffId());
        assertTrue(result.getStaffId().startsWith("STF"));
        assertEquals("System Admin", result.getEmpName());
    }

    @Test
    void testAddBankOfficer_SuccessWithBranchCode() {
        BankOfficerRequestDto officerDto = new BankOfficerRequestDto();
        officerDto.setUserId("USR1002");
        officerDto.setEmpName("John Officer");
        officerDto.setEmpPhone("9876543211");
        officerDto.setEmpDob(LocalDate.of(1992, 5, 10));
        officerDto.setEmpAddress("456 Bank Blvd");
        officerDto.setEmpDesignation("Senior Branch Officer");
        officerDto.setEmpJoiningDate(LocalDate.of(2021, 3, 15));
        officerDto.setEmpStatus("ACTIVE");
        officerDto.setBranchCode("BR-MUMBAI-01");

        when(userRepository.existsById("USR1002")).thenReturn(true);
        when(staffRepository.existsById(any())).thenReturn(false);
        when(bankOfficerRepository.save(any(BankOfficer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankOfficerResponseDto result = staffService.addBankOfficer(officerDto);

        assertNotNull(result);
        assertNotNull(result.getStaffId());
        assertEquals("BR-MUMBAI-01", result.getBranchCode());
    }
}
