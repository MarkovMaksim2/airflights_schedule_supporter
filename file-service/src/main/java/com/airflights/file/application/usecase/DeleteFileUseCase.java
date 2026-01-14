package com.airflights.file.application.usecase;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.event.FileDeletedEvent;
import com.airflights.file.domain.port.FileEventPublisher;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DeleteFileUseCase {

    private final FileRepository fileRepository;
    private final ObjectStorage objectStorage;
    private final FileEventPublisher fileEventPublisher;

    public void delete(String fileId) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
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
