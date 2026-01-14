package com.airflights.notification.interfaces.kafka.dto;

public record FileUploadedEvent(
        String eventId,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String contentType,
        long sizeBytes,
        String eventType
) {}
