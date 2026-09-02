package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;

import java.util.List;

public interface IStaffService {

    AdminDto addAdmin(AdminDto adminDto);

    AdminDto getAdminById(String staffId);

    List<AdminDto> getAllAdmins();

    BankOfficerDto addBankOfficer(BankOfficerDto bankOfficerDto);

    BankOfficerDto getBankOfficerById(String staffId);

    List<BankOfficerDto> getAllBankOfficers();

    FraudAnalystDto addFraudAnalyst(FraudAnalystDto fraudAnalystDto);

    FraudAnalystDto getFraudAnalystById(String staffId);

    List<FraudAnalystDto> getAllFraudAnalysts();

    CustomerServiceAgentDto addCustomerServiceAgent(CustomerServiceAgentDto agentDto);

    CustomerServiceAgentDto getCustomerServiceAgentById(String staffId);

    List<CustomerServiceAgentDto> getAllCustomerServiceAgents();

    StaffDto getStaffById(String staffId);

    List<StaffDto> getAllStaff();
}
