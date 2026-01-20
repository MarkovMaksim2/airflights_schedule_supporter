package com.airflights.notification.application.event;

public record FileDeletedEvent(
        String eventId,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String eventType
) {}
