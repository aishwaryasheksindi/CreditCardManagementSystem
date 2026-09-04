package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.DocumentType;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;

import java.time.LocalDateTime;

public class KycDocumentResponseDto {

    private String kycDocumentId;
    private String customerId;
    private DocumentType documentType;
    private String documentNumber;
    private String documentUrl;
    private KycStatus status;
    private LocalDateTime submittedAt;
    private String verifiedByStaffId;
    private LocalDateTime verifiedAt;
    private String rejectionReason;

    public KycDocumentResponseDto() {
    }

    public KycDocumentResponseDto(String kycDocumentId,
                                 String customerId,
                                 DocumentType documentType,
                                 String documentNumber,
                                 String documentUrl,
                                 KycStatus status,
                                 LocalDateTime submittedAt,
                                 String verifiedByStaffId,
                                 LocalDateTime verifiedAt,
                                 String rejectionReason) {
        this.kycDocumentId = kycDocumentId;
        this.customerId = customerId;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.documentUrl = documentUrl;
        this.status = status;
        this.submittedAt = submittedAt;
        this.verifiedByStaffId = verifiedByStaffId;
        this.verifiedAt = verifiedAt;
        this.rejectionReason = rejectionReason;
    }

    public String getKycDocumentId() {
        return kycDocumentId;
    }

    public void setKycDocumentId(String kycDocumentId) {
        this.kycDocumentId = kycDocumentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public KycStatus getStatus() {
        return status;
    }

    public void setStatus(KycStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getVerifiedByStaffId() {
        return verifiedByStaffId;
    }

    public void setVerifiedByStaffId(String verifiedByStaffId) {
        this.verifiedByStaffId = verifiedByStaffId;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
