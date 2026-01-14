package com.airflights.file.domain.model;

import java.time.LocalDateTime;

public record FileMetadata(
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String contentType,
        long sizeBytes,
        LocalDateTime createdAt
) {}
