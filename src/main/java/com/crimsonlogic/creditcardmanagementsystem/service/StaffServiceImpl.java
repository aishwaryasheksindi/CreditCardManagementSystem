package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.entity.*;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.*;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffServiceImpl implements IStaffService {

    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;
    private final BankOfficerRepository bankOfficerRepository;
    private final FraudAnalystRepository fraudAnalystRepository;
    private final CustomerServiceAgentRepository customerServiceAgentRepository;
    private final UserRepository userRepository;

    public StaffServiceImpl(StaffRepository staffRepository,
                            AdminRepository adminRepository,
                            BankOfficerRepository bankOfficerRepository,
                            FraudAnalystRepository fraudAnalystRepository,
                            CustomerServiceAgentRepository customerServiceAgentRepository,
                            UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
        this.bankOfficerRepository = bankOfficerRepository;
        this.fraudAnalystRepository = fraudAnalystRepository;
        this.customerServiceAgentRepository = customerServiceAgentRepository;
        this.userRepository = userRepository;
    }

    private String generateUniqueStaffId() {
        String staffId;
        do {
            staffId = IdGenerationUtil.generateStaffId();
        } while (staffRepository.existsById(staffId));
        return staffId;
    }

    private void validateUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public AdminResponseDto addAdmin(AdminRequestDto adminDto) {
        validateUser(adminDto.getUserId());

        Admin admin = new Admin();
        admin.setStaffId(generateUniqueStaffId());
        admin.setUserId(adminDto.getUserId());
        admin.setEmpName(adminDto.getEmpName());
        admin.setEmpPhone(adminDto.getEmpPhone());
        admin.setEmpDob(adminDto.getEmpDob());
        admin.setEmpAddress(adminDto.getEmpAddress());
        admin.setEmpDesignation(adminDto.getEmpDesignation());
        admin.setEmpJoiningDate(adminDto.getEmpJoiningDate());
        admin.setEmpStatus(adminDto.getEmpStatus());

        Admin saved = adminRepository.save(admin);
        return convertToAdminResponseDto(saved);
    }

    @Override
    public AdminResponseDto getAdminById(String staffId) {
        Admin admin = adminRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + staffId));
        return convertToAdminResponseDto(admin);
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::convertToAdminResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankOfficerResponseDto addBankOfficer(BankOfficerRequestDto bankOfficerDto) {
        validateUser(bankOfficerDto.getUserId());

        BankOfficer officer = new BankOfficer();
        officer.setStaffId(generateUniqueStaffId());
        officer.setUserId(bankOfficerDto.getUserId());
        officer.setEmpName(bankOfficerDto.getEmpName());
        officer.setEmpPhone(bankOfficerDto.getEmpPhone());
        officer.setEmpDob(bankOfficerDto.getEmpDob());
        officer.setEmpAddress(bankOfficerDto.getEmpAddress());
        officer.setEmpDesignation(bankOfficerDto.getEmpDesignation());
        officer.setEmpJoiningDate(bankOfficerDto.getEmpJoiningDate());
        officer.setEmpStatus(bankOfficerDto.getEmpStatus());
        officer.setBranchCode(bankOfficerDto.getBranchCode());

        BankOfficer saved = bankOfficerRepository.save(officer);
        return convertToBankOfficerResponseDto(saved);
    }

    @Override
    public BankOfficerResponseDto getBankOfficerById(String staffId) {
        BankOfficer officer = bankOfficerRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank officer not found with ID: " + staffId));
        return convertToBankOfficerResponseDto(officer);
    }

    @Override
    public List<BankOfficerResponseDto> getAllBankOfficers() {
        return bankOfficerRepository.findAll().stream()
                .map(this::convertToBankOfficerResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FraudAnalystResponseDto addFraudAnalyst(FraudAnalystRequestDto fraudAnalystDto) {
        validateUser(fraudAnalystDto.getUserId());

        FraudAnalyst analyst = new FraudAnalyst();
        analyst.setStaffId(generateUniqueStaffId());
        analyst.setUserId(fraudAnalystDto.getUserId());
        analyst.setEmpName(fraudAnalystDto.getEmpName());
        analyst.setEmpPhone(fraudAnalystDto.getEmpPhone());
        analyst.setEmpDob(fraudAnalystDto.getEmpDob());
        analyst.setEmpAddress(fraudAnalystDto.getEmpAddress());
        analyst.setEmpDesignation(fraudAnalystDto.getEmpDesignation());
        analyst.setEmpJoiningDate(fraudAnalystDto.getEmpJoiningDate());
        analyst.setEmpStatus(fraudAnalystDto.getEmpStatus());

        FraudAnalyst saved = fraudAnalystRepository.save(analyst);
        return convertToFraudAnalystResponseDto(saved);
    }

    @Override
    public FraudAnalystResponseDto getFraudAnalystById(String staffId) {
        FraudAnalyst analyst = fraudAnalystRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud analyst not found with ID: " + staffId));
        return convertToFraudAnalystResponseDto(analyst);
    }

    @Override
    public List<FraudAnalystResponseDto> getAllFraudAnalysts() {
        return fraudAnalystRepository.findAll().stream()
                .map(this::convertToFraudAnalystResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerServiceAgentResponseDto addCustomerServiceAgent(CustomerServiceAgentRequestDto agentDto) {
        validateUser(agentDto.getUserId());

        CustomerServiceAgent agent = new CustomerServiceAgent();
        agent.setStaffId(generateUniqueStaffId());
        agent.setUserId(agentDto.getUserId());
        agent.setEmpName(agentDto.getEmpName());
        agent.setEmpPhone(agentDto.getEmpPhone());
        agent.setEmpDob(agentDto.getEmpDob());
        agent.setEmpAddress(agentDto.getEmpAddress());
        agent.setEmpDesignation(agentDto.getEmpDesignation());
        agent.setEmpJoiningDate(agentDto.getEmpJoiningDate());
        agent.setEmpStatus(agentDto.getEmpStatus());

        CustomerServiceAgent saved = customerServiceAgentRepository.save(agent);
        return convertToCustomerServiceAgentResponseDto(saved);
    }

    @Override
    public CustomerServiceAgentResponseDto getCustomerServiceAgentById(String staffId) {
        CustomerServiceAgent agent = customerServiceAgentRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer service agent not found with ID: " + staffId));
        return convertToCustomerServiceAgentResponseDto(agent);
    }

    @Override
    public List<CustomerServiceAgentResponseDto> getAllCustomerServiceAgents() {
        return customerServiceAgentRepository.findAll().stream()
                .map(this::convertToCustomerServiceAgentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponseDto getStaffById(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + staffId));
        return convertToGenericStaffResponseDto(staff);
    }

    @Override
    public List<StaffResponseDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToGenericStaffResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StaffResponseDto> searchStaff(String empName, String empPhone) {
        java.util.List<Staff> results = new java.util.ArrayList<>();

        if (empName != null && !empName.isBlank()) {
            results.addAll(staffRepository.findByEmpNameContainingIgnoreCase(empName.trim()));
        }

        if (empPhone != null && !empPhone.isBlank()) {
            results.addAll(staffRepository.findByEmpPhone(empPhone.trim()));
        }

        if ((empName == null || empName.isBlank())
                && (empPhone == null || empPhone.isBlank())) {
            results.addAll(staffRepository.findAll());
        }

        java.util.Set<String> seenIds = new java.util.LinkedHashSet<>();
        java.util.List<Staff> distinctResults = new java.util.ArrayList<>();
        for (Staff s : results) {
            if (seenIds.add(s.getStaffId())) {
                distinctResults.add(s);
            }
        }

        return distinctResults.stream()
                .map(this::convertToGenericStaffResponseDto)
                .collect(Collectors.toList());
    }

    private AdminResponseDto convertToAdminResponseDto(Admin admin) {
        AdminResponseDto dto = new AdminResponseDto();
        populateBaseResponseDto(dto, admin);
        return dto;
    }

    private BankOfficerResponseDto convertToBankOfficerResponseDto(BankOfficer officer) {
        BankOfficerResponseDto dto = new BankOfficerResponseDto();
        populateBaseResponseDto(dto, officer);
        dto.setBranchCode(officer.getBranchCode());
        return dto;
    }

    private FraudAnalystResponseDto convertToFraudAnalystResponseDto(FraudAnalyst analyst) {
        FraudAnalystResponseDto dto = new FraudAnalystResponseDto();
        populateBaseResponseDto(dto, analyst);
        return dto;
    }

    private CustomerServiceAgentResponseDto convertToCustomerServiceAgentResponseDto(CustomerServiceAgent agent) {
        CustomerServiceAgentResponseDto dto = new CustomerServiceAgentResponseDto();
        populateBaseResponseDto(dto, agent);
        return dto;
    }

    private StaffResponseDto convertToGenericStaffResponseDto(Staff staff) {
        if (staff instanceof Admin admin) {
            return convertToAdminResponseDto(admin);
        } else if (staff instanceof BankOfficer officer) {
            return convertToBankOfficerResponseDto(officer);
        } else if (staff instanceof FraudAnalyst analyst) {
            return convertToFraudAnalystResponseDto(analyst);
        } else if (staff instanceof CustomerServiceAgent agent) {
            return convertToCustomerServiceAgentResponseDto(agent);
        }
        StaffResponseDto dto = new StaffResponseDto();
        populateBaseResponseDto(dto, staff);
        return dto;
    }

    private void populateBaseResponseDto(StaffResponseDto dto, Staff staff) {
        dto.setStaffId(staff.getStaffId());
        dto.setUserId(staff.getUserId());
        dto.setEmpName(staff.getEmpName());
        dto.setEmpPhone(staff.getEmpPhone());
        dto.setEmpDob(staff.getEmpDob());
        dto.setEmpAddress(staff.getEmpAddress());
        dto.setEmpDesignation(staff.getEmpDesignation());
        dto.setEmpJoiningDate(staff.getEmpJoiningDate());
        dto.setEmpStatus(staff.getEmpStatus());
    }
}
