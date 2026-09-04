package com.crimsonlogic.creditcardmanagementsystem.entity;

import com.crimsonlogic.creditcardmanagementsystem.enums.NotificationChannel;
import com.crimsonlogic.creditcardmanagementsystem.enums.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    private String notificationId;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Column(length = 1000)
    private String message;

    private String deliveryStatus;

    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(String notificationId,
                        String customerId,
                        NotificationType type,
                        NotificationChannel channel,
                        String message,
                        String deliveryStatus,
                        LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.deliveryStatus = deliveryStatus;
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
