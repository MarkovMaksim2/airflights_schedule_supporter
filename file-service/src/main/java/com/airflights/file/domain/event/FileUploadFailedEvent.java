package com.airflights.file.domain.event;

public record FileUploadFailedEvent(
        String eventId,
        String eventType,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String contentType,
        long sizeBytes,
        String reason
) {}
