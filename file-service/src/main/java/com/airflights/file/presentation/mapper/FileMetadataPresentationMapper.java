package com.airflights.file.presentation.mapper;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.presentation.dto.FileMetadataResponse;
import org.springframework.stereotype.Component;

@Component
public class FileMetadataPresentationMapper {
    public FileMetadataResponse toResponse(FileMetadata metadata) {
        return new FileMetadataResponse(
                metadata.fileId(),
                metadata.ownerId(),
                metadata.bucket(),
                metadata.objectKey(),
                metadata.contentType(),
                metadata.sizeBytes(),
                metadata.createdAt()
        );
    }
}
