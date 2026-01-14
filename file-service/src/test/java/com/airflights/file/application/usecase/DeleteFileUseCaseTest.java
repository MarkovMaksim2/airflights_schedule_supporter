package com.airflights.file.application.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.file.domain.event.FileDeletedEvent;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileEventPublisher;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class DeleteFileUseCaseTest {

    @Test
    void delete_publishesEvent() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        ObjectStorage objectStorage = Mockito.mock(ObjectStorage.class);
        FileEventPublisher fileEventPublisher = Mockito.mock(FileEventPublisher.class);
        DeleteFileUseCase useCase = new DeleteFileUseCase(fileRepository, objectStorage, fileEventPublisher);

        FileMetadata metadata = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                10L,
                LocalDateTime.now()
        );
        when(fileRepository.findById("file-1")).thenReturn(Optional.of(metadata));

        useCase.delete("file-1");

        verify(objectStorage).deleteObject(metadata.bucket(), metadata.objectKey());
        verify(fileRepository).deleteById("file-1");
        ArgumentCaptor<FileDeletedEvent> eventCaptor = ArgumentCaptor.forClass(FileDeletedEvent.class);
        verify(fileEventPublisher).publishFileDeleted(eventCaptor.capture());
        assertEquals("FileDeleted", eventCaptor.getValue().eventType());
    }

    @Test
    void delete_missing_throws() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        ObjectStorage objectStorage = Mockito.mock(ObjectStorage.class);
        FileEventPublisher fileEventPublisher = Mockito.mock(FileEventPublisher.class);
        DeleteFileUseCase useCase = new DeleteFileUseCase(fileRepository, objectStorage, fileEventPublisher);

        when(fileRepository.findById("file-1")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> useCase.delete("file-1"));
    }
}
