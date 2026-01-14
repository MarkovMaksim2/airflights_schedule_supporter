package com.airflights.notification.domain.model;

import java.time.LocalDateTime;

public record Notification(
        Long id,
        String userId,
        String eventId,
        NotificationType type,
        String payload,
        NotificationStatus status,
        int retries,
        LocalDateTime createdAt
) {}
