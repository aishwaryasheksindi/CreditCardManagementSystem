package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Notification;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.NotificationRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   CustomerRepository customerRepository) {
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    private String generateUniqueNotificationId() {
        String notificationId;
        do {
            notificationId = IdGenerationUtil.generateNotificationId();
        } while (notificationRepository.existsById(notificationId));
        return notificationId;
    }

    private void validateCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public NotificationResponseDto sendNotification(NotificationRequestDto requestDto) {
        validateCustomer(requestDto.getCustomerId());

        Notification notification = new Notification();
        notification.setNotificationId(generateUniqueNotificationId());
        notification.setCustomerId(requestDto.getCustomerId());
        notification.setType(requestDto.getType());
        notification.setChannel(requestDto.getChannel());
        notification.setMessage(requestDto.getMessage());
        notification.setDeliveryStatus(
                requestDto.getDeliveryStatus() != null && !requestDto.getDeliveryStatus().isBlank()
                        ? requestDto.getDeliveryStatus().trim()
                        : "SENT"
        );
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return convertToResponseDto(saved);
    }

    @Override
    public NotificationResponseDto getNotificationById(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
        return convertToResponseDto(notification);
    }

    @Override
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByCustomerId(String customerId) {
        return notificationRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDto updateDeliveryStatus(String notificationId, String deliveryStatus) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notification.setDeliveryStatus(deliveryStatus);
        Notification saved = notificationRepository.save(notification);
        return convertToResponseDto(saved);
    }

    @Override
    public void deleteNotification(String notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    private NotificationResponseDto convertToResponseDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getNotificationId(),
                notification.getCustomerId(),
                notification.getType(),
                notification.getChannel(),
                notification.getMessage(),
                notification.getDeliveryStatus(),
                notification.getCreatedAt()
        );
    }
}
