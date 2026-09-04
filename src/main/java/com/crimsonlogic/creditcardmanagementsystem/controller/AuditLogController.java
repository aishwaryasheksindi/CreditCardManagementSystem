package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.AuditLogResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.IAuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final IAuditLogService auditLogService;

    public AuditLogController(IAuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public ResponseEntity<AuditLogResponseDto> createAuditLog(@Valid @RequestBody AuditLogRequestDto requestDto) {
        AuditLogResponseDto created = auditLogService.createAuditLog(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{auditLogId}")
    public ResponseEntity<AuditLogResponseDto> getAuditLogById(@PathVariable String auditLogId) {
        return ResponseEntity.ok(auditLogService.getAuditLogById(auditLogId));
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAllAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId) {
        if (entityType != null && entityId != null) {
            return ResponseEntity.ok(auditLogService.getAuditLogsByEntity(entityType, entityId));
        }
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    @DeleteMapping("/{auditLogId}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable String auditLogId) {
        auditLogService.deleteAuditLog(auditLogId);
        return ResponseEntity.noContent().build();
    }
}
