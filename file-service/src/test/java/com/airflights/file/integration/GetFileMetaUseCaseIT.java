package com.airflights.file.integration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.file.application.usecase.GetFileMetaUseCase;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GetFileMetaUseCaseIT.TestConfig.class)
class GetFileMetaUseCaseIT {

    @Configuration
    static class TestConfig {
        @Bean
        GetFileMetaUseCase getFileMetaUseCase(FileRepository repository) {
            return new GetFileMetaUseCase(repository);
        }
    }

    @MockBean
    private FileRepository fileRepository;

    @Autowired
    private GetFileMetaUseCase getFileMetaUseCase;

    @Test
    void getById_usesSpringContext() {
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

        getFileMetaUseCase.getById("file-1");

        verify(fileRepository).findById("file-1");
    }
}
