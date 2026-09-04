package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.NotificationResponseDto;

import java.util.List;

public interface INotificationService {

    NotificationResponseDto sendNotification(NotificationRequestDto requestDto);

    NotificationResponseDto getNotificationById(String notificationId);

    List<NotificationResponseDto> getAllNotifications();

    List<NotificationResponseDto> getNotificationsByCustomerId(String customerId);

    NotificationResponseDto updateDeliveryStatus(String notificationId, String deliveryStatus);

    void deleteNotification(String notificationId);
}
