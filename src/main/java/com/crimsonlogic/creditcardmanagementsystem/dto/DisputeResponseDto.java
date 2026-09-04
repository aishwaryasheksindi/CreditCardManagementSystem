package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;

import java.time.LocalDateTime;

public class DisputeResponseDto {

    private String disputeId;
    private String transactionId;
    private String customerId;
    private DisputeType disputeType;
    private DisputeStatus status;
    private String description;
    private String evidenceReference;
    private String investigatorStaffId;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;
    private String resolution;

    public DisputeResponseDto() {
    }

    public DisputeResponseDto(String disputeId,
                              String transactionId,
                              String customerId,
                              DisputeType disputeType,
                              DisputeStatus status,
                              String description,
                              String evidenceReference,
                              String investigatorStaffId,
                              LocalDateTime raisedAt,
                              LocalDateTime resolvedAt,
                              String resolution) {
        this.disputeId = disputeId;
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.disputeType = disputeType;
        this.status = status;
        this.description = description;
        this.evidenceReference = evidenceReference;
        this.investigatorStaffId = investigatorStaffId;
        this.raisedAt = raisedAt;
        this.resolvedAt = resolvedAt;
        this.resolution = resolution;
    }

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public DisputeType getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(DisputeType disputeType) {
        this.disputeType = disputeType;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }

    public String getInvestigatorStaffId() {
        return investigatorStaffId;
    }

    public void setInvestigatorStaffId(String investigatorStaffId) {
        this.investigatorStaffId = investigatorStaffId;
    }

    public LocalDateTime getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(LocalDateTime raisedAt) {
        this.raisedAt = raisedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
