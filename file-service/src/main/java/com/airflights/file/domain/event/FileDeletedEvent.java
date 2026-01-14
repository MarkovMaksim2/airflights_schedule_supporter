package com.airflights.file.domain.event;

public record FileDeletedEvent(
        String eventId,
        String eventType,
        String fileId,
        String ownerId,
        String bucket,
        String objectKey
) {}
