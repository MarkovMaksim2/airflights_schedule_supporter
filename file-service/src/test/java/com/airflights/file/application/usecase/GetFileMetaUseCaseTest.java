package com.airflights.file.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class GetFileMetaUseCaseTest {

    @Test
    void getById_success() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        GetFileMetaUseCase useCase = new GetFileMetaUseCase(fileRepository);

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

        FileMetadata result = useCase.getById("file-1");

        assertEquals("file-1", result.fileId());
    }

    @Test
    void getById_missing_throws() {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        GetFileMetaUseCase useCase = new GetFileMetaUseCase(fileRepository);

        when(fileRepository.findById("file-1")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> useCase.getById("file-1"));
    }
}
