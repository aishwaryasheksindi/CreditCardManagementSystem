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
    public ResponseEntity<AdminDto> addAdmin(@Valid @RequestBody AdminDto adminDto) {
        AdminDto saved = staffService.addAdmin(adminDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/admins/{staffId}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getAdminById(staffId));
    }

    @GetMapping("/admins")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        return ResponseEntity.ok(staffService.getAllAdmins());
    }

    // Bank Officer endpoints
    @PostMapping("/bank-officers")
    public ResponseEntity<BankOfficerDto> addBankOfficer(@Valid @RequestBody BankOfficerDto bankOfficerDto) {
        BankOfficerDto saved = staffService.addBankOfficer(bankOfficerDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/bank-officers/{staffId}")
    public ResponseEntity<BankOfficerDto> getBankOfficerById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getBankOfficerById(staffId));
    }

    @GetMapping("/bank-officers")
    public ResponseEntity<List<BankOfficerDto>> getAllBankOfficers() {
        return ResponseEntity.ok(staffService.getAllBankOfficers());
    }

    // Fraud Analyst endpoints
    @PostMapping("/fraud-analysts")
    public ResponseEntity<FraudAnalystDto> addFraudAnalyst(@Valid @RequestBody FraudAnalystDto fraudAnalystDto) {
        FraudAnalystDto saved = staffService.addFraudAnalyst(fraudAnalystDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/fraud-analysts/{staffId}")
    public ResponseEntity<FraudAnalystDto> getFraudAnalystById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getFraudAnalystById(staffId));
    }

    @GetMapping("/fraud-analysts")
    public ResponseEntity<List<FraudAnalystDto>> getAllFraudAnalysts() {
        return ResponseEntity.ok(staffService.getAllFraudAnalysts());
    }

    // Customer Service Agent endpoints
    @PostMapping("/customer-service-agents")
    public ResponseEntity<CustomerServiceAgentDto> addCustomerServiceAgent(@Valid @RequestBody CustomerServiceAgentDto agentDto) {
        CustomerServiceAgentDto saved = staffService.addCustomerServiceAgent(agentDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/customer-service-agents/{staffId}")
    public ResponseEntity<CustomerServiceAgentDto> getCustomerServiceAgentById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getCustomerServiceAgentById(staffId));
    }

    @GetMapping("/customer-service-agents")
    public ResponseEntity<List<CustomerServiceAgentDto>> getAllCustomerServiceAgents() {
        return ResponseEntity.ok(staffService.getAllCustomerServiceAgents());
    }

    // General Staff endpoints
    @GetMapping("/{staffId}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable String staffId) {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }

    @GetMapping
    public ResponseEntity<List<StaffDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }
}
