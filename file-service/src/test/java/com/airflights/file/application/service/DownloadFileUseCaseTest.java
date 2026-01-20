package com.airflights.file.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.airflights.file.application.exception.ResourceNotFoundException;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DownloadFileUseCaseTest {

    @Test
    void download_success() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        ObjectStorage objectStorage = Mockito.mock(ObjectStorage.class);
        DownloadFileUseCase useCase = new DownloadFileUseCase(fileRepository, objectStorage);

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
        when(objectStorage.getObject(metadata.bucket(), metadata.objectKey()))
                .thenReturn(new ByteArrayInputStream(new byte[] {1, 2}));

        DownloadFileUseCase.FileDownloadResult result = useCase.download("file-1");

        assertEquals("file-1", result.metadata().fileId());
    }

    @Test
    void download_missing_throws() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        ObjectStorage objectStorage = Mockito.mock(ObjectStorage.class);
        DownloadFileUseCase useCase = new DownloadFileUseCase(fileRepository, objectStorage);

        when(fileRepository.findById("file-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.download("file-1"));
    }
}
