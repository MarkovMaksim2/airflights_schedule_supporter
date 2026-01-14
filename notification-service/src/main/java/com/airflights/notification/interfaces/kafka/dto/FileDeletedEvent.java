package com.airflights.notification.interfaces.kafka.dto;

public record FileDeletedEvent(
        String eventId,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String eventType
) {}
