package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;

import java.util.List;

public interface IStaffService {

    AdminResponseDto addAdmin(AdminRequestDto adminDto);

    AdminResponseDto getAdminById(String staffId);

    List<AdminResponseDto> getAllAdmins();

    BankOfficerResponseDto addBankOfficer(BankOfficerRequestDto bankOfficerDto);

    BankOfficerResponseDto getBankOfficerById(String staffId);

    List<BankOfficerResponseDto> getAllBankOfficers();

    FraudAnalystResponseDto addFraudAnalyst(FraudAnalystRequestDto fraudAnalystDto);

    FraudAnalystResponseDto getFraudAnalystById(String staffId);

    List<FraudAnalystResponseDto> getAllFraudAnalysts();

    CustomerServiceAgentResponseDto addCustomerServiceAgent(CustomerServiceAgentRequestDto agentDto);

    CustomerServiceAgentResponseDto getCustomerServiceAgentById(String staffId);

    List<CustomerServiceAgentResponseDto> getAllCustomerServiceAgents();

    StaffResponseDto getStaffById(String staffId);

    List<StaffResponseDto> getAllStaff();

    List<StaffResponseDto> searchStaff(String empName, String empPhone);
}
