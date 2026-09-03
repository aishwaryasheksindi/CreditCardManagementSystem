package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.*;
import com.crimsonlogic.creditcardmanagementsystem.service.IStaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final IStaffService staffService;

    public StaffController(IStaffService staffService) {
        this.staffService = staffService;
    }

    // Admin endpoints
    @PostMapping("/admins")
    public ResponseEntity<AdminResponseDto> addAdmin(@Valid @RequestBody AdminRequestDto adminDto) {
        AdminResponseDto saved = staffService.addAdmin(adminDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/admins/{staffId}")
    public ResponseEntity<AdminResponseDto> getAdminById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getAdminById(staffId));
    }

    @GetMapping("/admins")
    public ResponseEntity<List<AdminResponseDto>> getAllAdmins() {
        return ResponseEntity.ok(staffService.getAllAdmins());
    }

    // Bank Officer endpoints
    @PostMapping("/bank-officers")
    public ResponseEntity<BankOfficerResponseDto> addBankOfficer(@Valid @RequestBody BankOfficerRequestDto bankOfficerDto) {
        BankOfficerResponseDto saved = staffService.addBankOfficer(bankOfficerDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/bank-officers/{staffId}")
    public ResponseEntity<BankOfficerResponseDto> getBankOfficerById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getBankOfficerById(staffId));
    }

    @GetMapping("/bank-officers")
    public ResponseEntity<List<BankOfficerResponseDto>> getAllBankOfficers() {
        return ResponseEntity.ok(staffService.getAllBankOfficers());
    }

    // Fraud Analyst endpoints
    @PostMapping("/fraud-analysts")
    public ResponseEntity<FraudAnalystResponseDto> addFraudAnalyst(@Valid @RequestBody FraudAnalystRequestDto fraudAnalystDto) {
        FraudAnalystResponseDto saved = staffService.addFraudAnalyst(fraudAnalystDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/fraud-analysts/{staffId}")
    public ResponseEntity<FraudAnalystResponseDto> getFraudAnalystById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getFraudAnalystById(staffId));
    }

    @GetMapping("/fraud-analysts")
    public ResponseEntity<List<FraudAnalystResponseDto>> getAllFraudAnalysts() {
        return ResponseEntity.ok(staffService.getAllFraudAnalysts());
    }

    // Customer Service Agent endpoints
    @PostMapping("/customer-service-agents")
    public ResponseEntity<CustomerServiceAgentResponseDto> addCustomerServiceAgent(@Valid @RequestBody CustomerServiceAgentRequestDto agentDto) {
        CustomerServiceAgentResponseDto saved = staffService.addCustomerServiceAgent(agentDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/customer-service-agents/{staffId}")
    public ResponseEntity<CustomerServiceAgentResponseDto> getCustomerServiceAgentById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getCustomerServiceAgentById(staffId));
    }

    @GetMapping("/customer-service-agents")
    public ResponseEntity<List<CustomerServiceAgentResponseDto>> getAllCustomerServiceAgents() {
        return ResponseEntity.ok(staffService.getAllCustomerServiceAgents());
    }

    // General Staff endpoints
    @GetMapping("/{staffId}")
    public ResponseEntity<StaffResponseDto> getStaffById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }

    @GetMapping
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }
}
