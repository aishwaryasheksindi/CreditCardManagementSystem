package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;

import java.time.LocalDateTime;

public class AuditLogResponseDto {

    private String auditLogId;
    private String performedByUserId;
    private AuditAction action;
    private String entityType;
    private String entityId;
    private String description;
    private LocalDateTime timestamp;

    public AuditLogResponseDto() {
    }

    public AuditLogResponseDto(String auditLogId,
                              String performedByUserId,
                              AuditAction action,
                              String entityType,
                              String entityId,
                              String description,
                              LocalDateTime timestamp) {
        this.auditLogId = auditLogId;
        this.performedByUserId = performedByUserId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(String auditLogId) {
        this.auditLogId = auditLogId;
    }

    public String getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(String performedByUserId) {
        this.performedByUserId = performedByUserId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
