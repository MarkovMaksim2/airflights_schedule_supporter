package com.airflights.file.presentation.dto;

import java.time.LocalDateTime;

public record FileMetadataResponse(
        String fileId,
        String ownerId,
        String bucket,
        String objectKey,
        String contentType,
        long sizeBytes,
        LocalDateTime createdAt
) {}
