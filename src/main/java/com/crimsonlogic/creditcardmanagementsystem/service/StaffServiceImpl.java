package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.entity.*;
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
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    @Override
    public AdminDto addAdmin(AdminDto adminDto) {
        validateUser(adminDto.getUserId());

        Admin admin = new Admin();
        admin.setStaffId(generateUniqueStaffId());
        admin.setUserId(adminDto.getUserId());
        admin.setName(adminDto.getName());
        admin.setPhone(adminDto.getPhone());
        admin.setDob(adminDto.getDob());
        admin.setAddress(adminDto.getAddress());
        admin.setDesignation(adminDto.getDesignation());
        admin.setDateOfJoining(adminDto.getDateOfJoining());
        admin.setEmployeeStatus(adminDto.getEmployeeStatus());

        Admin saved = adminRepository.save(admin);
        return convertToAdminDto(saved);
    }

    @Override
    public AdminDto getAdminById(String staffId) {
        Admin admin = adminRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + staffId));
        return convertToAdminDto(admin);
    }

    @Override
    public List<AdminDto> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::convertToAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankOfficerDto addBankOfficer(BankOfficerDto bankOfficerDto) {
        validateUser(bankOfficerDto.getUserId());

        BankOfficer officer = new BankOfficer();
        officer.setStaffId(generateUniqueStaffId());
        officer.setUserId(bankOfficerDto.getUserId());
        officer.setName(bankOfficerDto.getName());
        officer.setPhone(bankOfficerDto.getPhone());
        officer.setDob(bankOfficerDto.getDob());
        officer.setAddress(bankOfficerDto.getAddress());
        officer.setDesignation(bankOfficerDto.getDesignation());
        officer.setDateOfJoining(bankOfficerDto.getDateOfJoining());
        officer.setEmployeeStatus(bankOfficerDto.getEmployeeStatus());
        officer.setBranchCode(bankOfficerDto.getBranchCode());

        BankOfficer saved = bankOfficerRepository.save(officer);
        return convertToBankOfficerDto(saved);
    }

    @Override
    public BankOfficerDto getBankOfficerById(String staffId) {
        BankOfficer officer = bankOfficerRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Bank officer not found with ID: " + staffId));
        return convertToBankOfficerDto(officer);
    }

    @Override
    public List<BankOfficerDto> getAllBankOfficers() {
        return bankOfficerRepository.findAll().stream()
                .map(this::convertToBankOfficerDto)
                .collect(Collectors.toList());
    }

    @Override
    public FraudAnalystDto addFraudAnalyst(FraudAnalystDto fraudAnalystDto) {
        validateUser(fraudAnalystDto.getUserId());

        FraudAnalyst analyst = new FraudAnalyst();
        analyst.setStaffId(generateUniqueStaffId());
        analyst.setUserId(fraudAnalystDto.getUserId());
        analyst.setName(fraudAnalystDto.getName());
        analyst.setPhone(fraudAnalystDto.getPhone());
        analyst.setDob(fraudAnalystDto.getDob());
        analyst.setAddress(fraudAnalystDto.getAddress());
        analyst.setDesignation(fraudAnalystDto.getDesignation());
        analyst.setDateOfJoining(fraudAnalystDto.getDateOfJoining());
        analyst.setEmployeeStatus(fraudAnalystDto.getEmployeeStatus());

        FraudAnalyst saved = fraudAnalystRepository.save(analyst);
        return convertToFraudAnalystDto(saved);
    }

    @Override
    public FraudAnalystDto getFraudAnalystById(String staffId) {
        FraudAnalyst analyst = fraudAnalystRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Fraud analyst not found with ID: " + staffId));
        return convertToFraudAnalystDto(analyst);
    }

    @Override
    public List<FraudAnalystDto> getAllFraudAnalysts() {
        return fraudAnalystRepository.findAll().stream()
                .map(this::convertToFraudAnalystDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerServiceAgentDto addCustomerServiceAgent(CustomerServiceAgentDto agentDto) {
        validateUser(agentDto.getUserId());

        CustomerServiceAgent agent = new CustomerServiceAgent();
        agent.setStaffId(generateUniqueStaffId());
        agent.setUserId(agentDto.getUserId());
        agent.setName(agentDto.getName());
        agent.setPhone(agentDto.getPhone());
        agent.setDob(agentDto.getDob());
        agent.setAddress(agentDto.getAddress());
        agent.setDesignation(agentDto.getDesignation());
        agent.setDateOfJoining(agentDto.getDateOfJoining());
        agent.setEmployeeStatus(agentDto.getEmployeeStatus());

        CustomerServiceAgent saved = customerServiceAgentRepository.save(agent);
        return convertToCustomerServiceAgentDto(saved);
    }

    @Override
    public CustomerServiceAgentDto getCustomerServiceAgentById(String staffId) {
        CustomerServiceAgent agent = customerServiceAgentRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Customer service agent not found with ID: " + staffId));
        return convertToCustomerServiceAgentDto(agent);
    }

    @Override
    public List<CustomerServiceAgentDto> getAllCustomerServiceAgents() {
        return customerServiceAgentRepository.findAll().stream()
                .map(this::convertToCustomerServiceAgentDto)
                .collect(Collectors.toList());
    }

    @Override
    public StaffDto getStaffById(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));
        return convertToGenericStaffDto(staff);
    }

    @Override
    public List<StaffDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToGenericStaffDto)
                .collect(Collectors.toList());
    }

    private AdminDto convertToAdminDto(Admin admin) {
        AdminDto dto = new AdminDto();
        populateBaseDto(dto, admin);
        return dto;
    }

    private BankOfficerDto convertToBankOfficerDto(BankOfficer officer) {
        BankOfficerDto dto = new BankOfficerDto();
        populateBaseDto(dto, officer);
        dto.setBranchCode(officer.getBranchCode());
        return dto;
    }

    private FraudAnalystDto convertToFraudAnalystDto(FraudAnalyst analyst) {
        FraudAnalystDto dto = new FraudAnalystDto();
        populateBaseDto(dto, analyst);
        return dto;
    }

    private CustomerServiceAgentDto convertToCustomerServiceAgentDto(CustomerServiceAgent agent) {
        CustomerServiceAgentDto dto = new CustomerServiceAgentDto();
        populateBaseDto(dto, agent);
        return dto;
    }

    private StaffDto convertToGenericStaffDto(Staff staff) {
        if (staff instanceof Admin admin) {
            return convertToAdminDto(admin);
        } else if (staff instanceof BankOfficer officer) {
            return convertToBankOfficerDto(officer);
        } else if (staff instanceof FraudAnalyst analyst) {
            return convertToFraudAnalystDto(analyst);
        } else if (staff instanceof CustomerServiceAgent agent) {
            return convertToCustomerServiceAgentDto(agent);
        }
        StaffDto dto = new StaffDto();
        populateBaseDto(dto, staff);
        return dto;
    }

    private void populateBaseDto(StaffDto dto, Staff staff) {
        dto.setStaffId(staff.getStaffId());
        dto.setUserId(staff.getUserId());
        dto.setName(staff.getName());
        dto.setPhone(staff.getPhone());
        dto.setDob(staff.getDob());
        dto.setAddress(staff.getAddress());
        dto.setDesignation(staff.getDesignation());
        dto.setDateOfJoining(staff.getDateOfJoining());
        dto.setEmployeeStatus(staff.getEmployeeStatus());
    }
}
