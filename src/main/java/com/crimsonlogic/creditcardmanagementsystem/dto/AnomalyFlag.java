package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.AnomalyType;

import java.time.LocalDateTime;

public class AnomalyFlag {

    private AnomalyType type;
    private String description;
    private String relatedTransactionId;
    private String severity;
    private LocalDateTime detectedAt;

    public AnomalyFlag() {
    }

    public AnomalyFlag(AnomalyType type, String description, String relatedTransactionId, String severity, LocalDateTime detectedAt) {
        this.type = type;
        this.description = description;
        this.relatedTransactionId = relatedTransactionId;
        this.severity = severity;
        this.detectedAt = detectedAt;
    }

    public AnomalyType getType() {
        return type;
    }

    public void setType(AnomalyType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(String relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}
