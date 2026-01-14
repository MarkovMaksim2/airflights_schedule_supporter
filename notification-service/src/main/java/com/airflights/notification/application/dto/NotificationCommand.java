package com.airflights.notification.application.dto;

import com.airflights.notification.domain.model.NotificationType;

public record NotificationCommand(
        String userId,
        String eventId,
        NotificationType type,
        String payload
) {}
