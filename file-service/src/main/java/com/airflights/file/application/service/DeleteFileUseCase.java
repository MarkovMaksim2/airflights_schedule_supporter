package com.airflights.file.application.service;

import com.airflights.file.application.exception.ResourceNotFoundException;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.event.FileDeletedEvent;
import com.airflights.file.domain.port.FileEventPublisher;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteFileUseCase {

    private final FileRepository fileRepository;
    private final ObjectStorage objectStorage;
    private final FileEventPublisher fileEventPublisher;

    public void delete(String fileId) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        objectStorage.deleteObject(metadata.bucket(), metadata.objectKey());
        fileRepository.deleteById(fileId);
        FileDeletedEvent event = new FileDeletedEvent(
                UUID.randomUUID().toString(),
                "FileDeleted",
                metadata.fileId(),
                metadata.ownerId(),
                metadata.bucket(),
                metadata.objectKey()
        );
        fileEventPublisher.publishFileDeleted(event);
    }
}
