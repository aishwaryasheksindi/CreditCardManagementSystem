package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.KycDocumentResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.entity.KycDocument;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.KycDocumentRepository;
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

    public KycDocumentServiceImpl(KycDocumentRepository kycDocumentRepository,
                                  CustomerRepository customerRepository,
                                  IAuditLogService auditLogService) {
        this.kycDocumentRepository = kycDocumentRepository;
        this.customerRepository = customerRepository;
        this.auditLogService = auditLogService;
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

        KycDocument document = new KycDocument();
        document.setKycDocumentId(generateUniqueKycDocumentId());
        document.setCustomerId(requestDto.getCustomerId());
        document.setDocumentType(requestDto.getDocumentType());
        document.setDocumentNumber(requestDto.getDocumentNumber());
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

    @Override
    public KycDocumentResponseDto verifyDocument(String kycDocumentId, String verifiedByStaffId) {
        KycDocument document = kycDocumentRepository.findById(kycDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId));

        document.setStatus(KycStatus.VERIFIED);
        document.setVerifiedByStaffId(verifiedByStaffId);
        document.setVerifiedAt(LocalDateTime.now());
        document.setRejectionReason(null);

        KycDocument saved = kycDocumentRepository.save(document);

        // Update linked Customer KYC status
        Customer customer = customerRepository.findById(document.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Linked customer not found with ID: " + document.getCustomerId()));
        customer.setKycStatus(KycStatus.VERIFIED);
        customerRepository.save(customer);

        auditLogService.logAction(verifiedByStaffId, AuditAction.STATUS_CHANGE, "KycDocument", kycDocumentId, "KYC document VERIFIED by staff " + verifiedByStaffId);

        return convertToResponseDto(saved);
    }

    @Override
    public KycDocumentResponseDto rejectDocument(String kycDocumentId, String verifiedByStaffId, String rejectionReason) {
        KycDocument document = kycDocumentRepository.findById(kycDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC Document not found with ID: " + kycDocumentId));

        document.setStatus(KycStatus.REJECTED);
        document.setVerifiedByStaffId(verifiedByStaffId);
        document.setVerifiedAt(LocalDateTime.now());
        document.setRejectionReason(rejectionReason);

        KycDocument saved = kycDocumentRepository.save(document);

        // Update linked Customer KYC status
        Customer customer = customerRepository.findById(document.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Linked customer not found with ID: " + document.getCustomerId()));
        customer.setKycStatus(KycStatus.REJECTED);
        customerRepository.save(customer);

        auditLogService.logAction(verifiedByStaffId, AuditAction.STATUS_CHANGE, "KycDocument", kycDocumentId, "KYC document REJECTED by staff " + verifiedByStaffId);

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
