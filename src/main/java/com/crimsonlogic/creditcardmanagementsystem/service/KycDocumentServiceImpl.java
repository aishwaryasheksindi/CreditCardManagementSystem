package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Admin;
import com.crimsonlogic.creditcardmanagementsystem.entity.BankOfficer;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.KycDocument;
import com.crimsonlogic.creditcardmanagementsystem.entity.Staff;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.DuplicateResourceException;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.KycDocumentRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.StaffRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.DocumentValidationUtil;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KycDocumentServiceImpl implements IKycDocumentService {

    private final KycDocumentRepository kycDocumentRepository;
    private final CustomerRepository customerRepository;
    private final IAuditLogService auditLogService;
    private final StaffRepository staffRepository;
    private final CurrentUserContext currentUserContext;

    public KycDocumentServiceImpl(KycDocumentRepository kycDocumentRepository,
                                  CustomerRepository customerRepository,
                                  IAuditLogService auditLogService,
                                  StaffRepository staffRepository,
                                  CurrentUserContext currentUserContext) {
        this.kycDocumentRepository = kycDocumentRepository;
        this.customerRepository = customerRepository;
        this.auditLogService = auditLogService;
        this.staffRepository = staffRepository;
        this.currentUserContext = currentUserContext;
    }

    private String generateUniqueKycDocumentId() {
        String kycDocumentId;
        do {
            kycDocumentId = IdGenerationUtil.generateKycDocumentId();
        } while (kycDocumentRepository.existsById(kycDocumentId));
        return kycDocumentId;
    }

    private void validateCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public KycDocumentResponseDto submitDocument(KycDocumentRequestDto requestDto) {
        validateCustomer(requestDto.getCustomerId());

        DocumentValidationUtil.validate(requestDto.getDocumentType(), requestDto.getDocumentNumber());

        String normalizedDocNumber = requestDto.getDocumentNumber().trim().toUpperCase();

        if (kycDocumentRepository.findByDocumentTypeAndDocumentNumberAndStatus(
                requestDto.getDocumentType(), normalizedDocNumber, KycStatus.VERIFIED).isPresent()) {
            throw new DuplicateResourceException(
                    "This " + requestDto.getDocumentType() + " is already verified against another customer account");
        }

        KycDocument document = new KycDocument();
        document.setKycDocumentId(generateUniqueKycDocumentId());
        document.setCustomerId(requestDto.getCustomerId());
        document.setDocumentType(requestDto.getDocumentType());
        document.setDocumentNumber(normalizedDocNumber);
        document.setDocumentUrl(requestDto.getDocumentUrl());
        document.setStatus(KycStatus.PENDING);
        document.setSubmittedAt(LocalDateTime.now());

        KycDocument saved = kycDocumentRepository.save(document);
        return convertToResponseDto(saved);
    }

    @Override
    public KycDocumentResponseDto getDocumentById(String kycDocumentId) {
        KycDocument document = kycDocumentRepository.findById(kycDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId));
        return convertToResponseDto(document);
    }

    @Override
    public List<KycDocumentResponseDto> getAllDocuments() {
        return kycDocumentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KycDocumentResponseDto> getDocumentsByCustomerId(String customerId) {
        return kycDocumentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private void validateKycAuthorization(Staff actingStaff, Customer customer) {
        if (actingStaff instanceof BankOfficer officer) {
            String officerBranch = officer.getBranchCode();
            String customerBranch = customer.getBranchCode();
            if (officerBranch == null || customerBranch == null || !officerBranch.equals(customerBranch)) {
                throw new AccessDeniedException(
                        "Bank officer is only authorized to verify or reject KYC documents for customers in their own branch");
            }
        } else if (actingStaff instanceof Admin) {
            // Admin is central/system-wide and exempt from branch restrictions
        } else {
            throw new AccessDeniedException(
                    "Only Admin or Bank Officer is authorized to verify or reject KYC documents");
        }
    }

    @Override
    public KycDocumentResponseDto verifyDocument(String kycDocumentId) {
        String actingUserId = currentUserContext.getCurrentUserId();
        Staff actingStaff = staffRepository.findByUserId(actingUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No staff record found for the authenticated user: " + actingUserId));
        String actingStaffId = actingStaff.getStaffId();

        KycDocument document = kycDocumentRepository.findById(kycDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId));

        Customer customer = customerRepository.findById(document.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Linked customer not found with ID: " + document.getCustomerId()));

        validateKycAuthorization(actingStaff, customer);

        // Validate document number format before marking as verified
        DocumentValidationUtil.validate(document.getDocumentType(), document.getDocumentNumber());

        document.setStatus(KycStatus.VERIFIED);
        document.setVerifiedByStaffId(actingStaffId);
        document.setVerifiedAt(LocalDateTime.now());
        document.setRejectionReason(null);

        KycDocument saved = kycDocumentRepository.save(document);

        // Update linked Customer KYC status
        customer.setKycStatus(KycStatus.VERIFIED);
        customerRepository.save(customer);

        auditLogService.logAction(actingStaffId, AuditAction.STATUS_CHANGE, "KycDocument", kycDocumentId, "KYC document VERIFIED by staff " + actingStaffId);

        return convertToResponseDto(saved);
    }

    @Override
    public KycDocumentResponseDto rejectDocument(String kycDocumentId, String rejectionReason) {
        String actingUserId = currentUserContext.getCurrentUserId();
        Staff actingStaff = staffRepository.findByUserId(actingUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No staff record found for the authenticated user: " + actingUserId));
        String actingStaffId = actingStaff.getStaffId();

        KycDocument document = kycDocumentRepository.findById(kycDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId));

        Customer customer = customerRepository.findById(document.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Linked customer not found with ID: " + document.getCustomerId()));

        validateKycAuthorization(actingStaff, customer);

        document.setStatus(KycStatus.REJECTED);
        document.setVerifiedByStaffId(actingStaffId);
        document.setVerifiedAt(LocalDateTime.now());
        document.setRejectionReason(rejectionReason);

        KycDocument saved = kycDocumentRepository.save(document);

        // Update linked Customer KYC status
        customer.setKycStatus(KycStatus.REJECTED);
        customerRepository.save(customer);

        auditLogService.logAction(actingStaffId, AuditAction.STATUS_CHANGE, "KycDocument", kycDocumentId, "KYC document REJECTED by staff " + actingStaffId);

        return convertToResponseDto(saved);
    }

    @Override
    public void deleteDocument(String kycDocumentId) {
        if (!kycDocumentRepository.existsById(kycDocumentId)) {
            throw new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId);
        }
        kycDocumentRepository.deleteById(kycDocumentId);
    }

    private KycDocumentResponseDto convertToResponseDto(KycDocument doc) {
        return new KycDocumentResponseDto(
                doc.getKycDocumentId(),
                doc.getCustomerId(),
                doc.getDocumentType(),
                doc.getDocumentNumber(),
                doc.getDocumentUrl(),
                doc.getStatus(),
                doc.getSubmittedAt(),
                doc.getVerifiedByStaffId(),
                doc.getVerifiedAt(),
                doc.getRejectionReason()
        );
    }
}
