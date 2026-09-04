package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;

import java.util.List;

public interface IAuditLogService {

    AuditLogResponseDto createAuditLog(AuditLogRequestDto requestDto);

    AuditLogResponseDto getAuditLogById(String auditLogId);

    List<AuditLogResponseDto> getAllAuditLogs();

    List<AuditLogResponseDto> getAuditLogsByEntity(String entityType, String entityId);

    void deleteAuditLog(String auditLogId);

    void logAction(String performedByUserId, AuditAction action, String entityType, String entityId, String description);
}
