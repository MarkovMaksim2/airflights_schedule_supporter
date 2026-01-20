package com.airflights.file.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.file.application.exception.BadRequestException;
import com.airflights.file.application.exception.FileUploadFailedException;
import com.airflights.file.application.exception.ForbiddenException;
import com.airflights.file.domain.event.FileUploadFailedEvent;
import com.airflights.file.domain.event.FileUploadedEvent;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileEventPublisher;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class UploadFileUseCaseTest {

    private ObjectStorage objectStorage;
    private FileRepository fileRepository;
    private FileEventPublisher fileEventPublisher;
    private UploadFileUseCase useCase;

    @BeforeEach
    void setUp() {
        objectStorage = Mockito.mock(ObjectStorage.class);
        fileRepository = Mockito.mock(FileRepository.class);
        fileEventPublisher = Mockito.mock(FileEventPublisher.class);
        useCase = new UploadFileUseCase(objectStorage, fileRepository, fileEventPublisher);
        ReflectionTestUtils.setField(useCase, "bucket", "airflights-files");
    }

    @Test
    void upload_success_publishesEventAndPersists() {
        FileMetadata stored = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                10L,
                LocalDateTime.now()
        );
        when(fileRepository.save(any(FileMetadata.class))).thenReturn(stored);

        FileMetadata result = useCase.upload(
                "user@example.com",
                "passport.pdf",
                "application/pdf",
                10L,
                new ByteArrayInputStream(new byte[] {1, 2})
        );

        assertEquals(stored.fileId(), result.fileId());
        ArgumentCaptor<FileUploadedEvent> eventCaptor = ArgumentCaptor.forClass(FileUploadedEvent.class);
        verify(fileEventPublisher).publishFileUploaded(eventCaptor.capture());
        assertEquals(stored.fileId(), eventCaptor.getValue().fileId());
        assertEquals("FileUploaded", eventCaptor.getValue().eventType());
    }

    @Test
    void upload_failure_publishesFailedEvent() {
        when(fileRepository.save(any(FileMetadata.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.doThrow(new RuntimeException("S3 down"))
                .when(objectStorage)
                .putObject(any(), any(), any(), anyLong(), any());

        FileUploadFailedException ex = assertThrows(
                FileUploadFailedException.class,
                () -> useCase.upload(
                        "user@example.com",
                        "passport.pdf",
                        "application/pdf",
                        10L,
                        new ByteArrayInputStream(new byte[] {1})
                )
        );

        assertNotNull(ex.getReason());
        verify(fileRepository, never()).save(any());
        ArgumentCaptor<FileUploadFailedEvent> failedCaptor = ArgumentCaptor.forClass(FileUploadFailedEvent.class);
        verify(fileEventPublisher).publishFileUploadFailed(failedCaptor.capture());
        assertEquals("user@example.com", failedCaptor.getValue().ownerId());
        assertEquals("FileUploadFailed", failedCaptor.getValue().eventType());
    }

    @Test
    void upload_missingOwner_throws() {
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> useCase.upload(
                        " ",
                        "passport.pdf",
                        "application/pdf",
                        10L,
                        new ByteArrayInputStream(new byte[] {1})
                )
        );
        Assertions.assertEquals("User email required", ex.getMessage());
        verify(fileRepository, never()).save(any());
        verify(fileEventPublisher, never()).publishFileUploaded(any());
    }

    @Test
    void upload_missingFilename_throws() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> useCase.upload(
                        "user@example.com",
                        " ",
                        "application/pdf",
                        10L,
                        new ByteArrayInputStream(new byte[] {1})
                )
        );
        Assertions.assertEquals("File name is required", ex.getMessage());
        verify(fileRepository, never()).save(any());
        verify(fileEventPublisher, never()).publishFileUploaded(any());
    }

    @Test
    void upload_nullContentType_defaultsToOctetStream() {
        when(fileRepository.save(any(FileMetadata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FileMetadata result = useCase.upload(
                "user@example.com",
                "passport.pdf",
                null,
                10L,
                new ByteArrayInputStream(new byte[] {1})
        );

        assertEquals("application/octet-stream", result.contentType());
        ArgumentCaptor<FileUploadedEvent> eventCaptor = ArgumentCaptor.forClass(FileUploadedEvent.class);
        verify(fileEventPublisher).publishFileUploaded(eventCaptor.capture());
        assertEquals("application/octet-stream", eventCaptor.getValue().contentType());
    }
}
