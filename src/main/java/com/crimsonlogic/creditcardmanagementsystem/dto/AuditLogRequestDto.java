package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuditLogRequestDto {

    @Size(max = 100, message = "User ID must not exceed 100 characters")
    private String performedByUserId;

    @NotNull(message = "Action is required")
    private AuditAction action;

    @NotBlank(message = "Entity type is required")
    @Size(max = 100, message = "Entity type must not exceed 100 characters")
    private String entityType;

    @NotBlank(message = "Entity ID is required")
    @Size(max = 100, message = "Entity ID must not exceed 100 characters")
    private String entityId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    public AuditLogRequestDto() {
    }

    public AuditLogRequestDto(String performedByUserId,
                             AuditAction action,
                             String entityType,
                             String entityId,
                             String description) {
        this.performedByUserId = performedByUserId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
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
}
