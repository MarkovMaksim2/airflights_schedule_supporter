package com.airflights.file.application.service;

import com.airflights.file.application.exception.BadRequestException;
import com.airflights.file.application.exception.FileUploadFailedException;
import com.airflights.file.application.exception.ForbiddenException;
import com.airflights.file.domain.event.FileUploadFailedEvent;
import com.airflights.file.domain.event.FileUploadedEvent;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileEventPublisher;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadFileUseCase {

    private final ObjectStorage objectStorage;
    private final FileRepository fileRepository;
    private final FileEventPublisher fileEventPublisher;

    @Value("${file.s3.bucket}")
    private String bucket;

    public FileMetadata upload(
            String ownerId,
            String originalFilename,
            String contentType,
            long size,
            InputStream content
    ) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new ForbiddenException("User email required");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("File name is required");
        }
        String fileId = UUID.randomUUID().toString();
        String resolvedContentType = (contentType == null || contentType.isBlank())
                ? "application/octet-stream"
                : contentType;
        String safeFilename = originalFilename.replaceAll("\\s+", "_");
        String objectKey = "user-uploads/" + ownerId + "/" + fileId + "/" + safeFilename;

        try {
            objectStorage.putObject(bucket, objectKey, content, size, resolvedContentType);
        } catch (RuntimeException ex) {
            FileUploadFailedEvent failedEvent = new FileUploadFailedEvent(
                    UUID.randomUUID().toString(),
                    "FileUploadFailed",
                    fileId,
                    ownerId,
                    bucket,
                    objectKey,
                    resolvedContentType,
                    size,
                    ex.getMessage()
            );
            fileEventPublisher.publishFileUploadFailed(failedEvent);
            throw new FileUploadFailedException("File upload failed", ex);
        }

        FileMetadata metadata = new FileMetadata(
                fileId,
                ownerId,
                bucket,
                objectKey,
                resolvedContentType,
                size,
                LocalDateTime.now()
        );
        FileMetadata saved = fileRepository.save(metadata);

        FileUploadedEvent event = new FileUploadedEvent(
                UUID.randomUUID().toString(),
                "FileUploaded",
                saved.fileId(),
                saved.ownerId(),
                saved.bucket(),
                saved.objectKey(),
                saved.contentType(),
                saved.sizeBytes()
        );
        fileEventPublisher.publishFileUploaded(event);

        return saved;
    }
}
