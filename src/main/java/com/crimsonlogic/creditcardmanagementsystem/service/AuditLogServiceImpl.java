package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.AuditLog;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.AuditLogRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements IAuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    private String generateUniqueAuditLogId() {
        String auditLogId;
        do {
            auditLogId = IdGenerationUtil.generateAuditLogId();
        } while (auditLogRepository.existsById(auditLogId));
        return auditLogId;
    }

    @Override
    public AuditLogResponseDto createAuditLog(AuditLogRequestDto requestDto) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditLogId(generateUniqueAuditLogId());
        auditLog.setPerformedByUserId(requestDto.getPerformedByUserId());
        auditLog.setAction(requestDto.getAction());
        auditLog.setEntityType(requestDto.getEntityType());
        auditLog.setEntityId(requestDto.getEntityId());
        auditLog.setDescription(requestDto.getDescription());
        auditLog.setTimestamp(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(auditLog);
        return convertToResponseDto(saved);
    }

    @Override
    public AuditLogResponseDto getAuditLogById(String auditLogId) {
        AuditLog auditLog = auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with ID: " + auditLogId));
        return convertToResponseDto(auditLog);
    }

    @Override
    public List<AuditLogResponseDto> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponseDto> getAuditLogsByEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAuditLog(String auditLogId) {
        if (!auditLogRepository.existsById(auditLogId)) {
            throw new ResourceNotFoundException("Audit log not found with ID: " + auditLogId);
        }
        auditLogRepository.deleteById(auditLogId);
    }

    @Override
    public void logAction(String performedByUserId, AuditAction action, String entityType, String entityId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditLogId(generateUniqueAuditLogId());
        auditLog.setPerformedByUserId(performedByUserId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    private AuditLogResponseDto convertToResponseDto(AuditLog auditLog) {
        return new AuditLogResponseDto(
                auditLog.getAuditLogId(),
                auditLog.getPerformedByUserId(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDescription(),
                auditLog.getTimestamp()
        );
    }
}
