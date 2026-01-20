package com.airflights.notification.application.event;

public record FileUploadFailedEvent(
        String eventId,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String contentType,
        long sizeBytes,
        String reason,
        String eventType
) {}
