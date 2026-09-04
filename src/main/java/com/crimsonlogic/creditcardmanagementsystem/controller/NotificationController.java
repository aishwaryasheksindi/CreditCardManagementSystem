package com.crimsonlogic.creditcardmanagementsystem.controller;

import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.service.INotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> sendNotification(@Valid @RequestBody NotificationRequestDto requestDto) {
        NotificationResponseDto created = notificationService.sendNotification(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable String notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(notificationService.getNotificationsByCustomerId(customerId));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PutMapping("/{notificationId}/status")
    public ResponseEntity<NotificationResponseDto> updateDeliveryStatus(
            @PathVariable String notificationId,
            @RequestParam String deliveryStatus) {
        return ResponseEntity.ok(notificationService.updateDeliveryStatus(notificationId, deliveryStatus));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
