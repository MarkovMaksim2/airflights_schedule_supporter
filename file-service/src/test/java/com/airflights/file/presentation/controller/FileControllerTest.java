package com.airflights.file.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.airflights.file.application.exception.BadRequestException;
import com.airflights.file.application.service.DeleteFileUseCase;
import com.airflights.file.application.service.DownloadFileUseCase;
import com.airflights.file.application.service.GetFileMetaUseCase;
import com.airflights.file.application.service.UploadFileUseCase;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.presentation.dto.FileMetadataResponse;
import com.airflights.file.presentation.mapper.FileMetadataPresentationMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private UploadFileUseCase uploadFileUseCase;

    @Mock
    private DownloadFileUseCase downloadFileUseCase;

    @Mock
    private GetFileMetaUseCase getFileMetaUseCase;

    @Mock
    private DeleteFileUseCase deleteFileUseCase;

    @InjectMocks
    private FileController fileController;

    @Spy
    private FileMetadataPresentationMapper fileMetadataPresentationMapper = new FileMetadataPresentationMapper();

    private FileMetadata metadata;

    @BeforeEach
    void setUp() {
        metadata = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                10L,
                LocalDateTime.now()
        );
    }

    @Test
    void upload_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "passport.pdf", "application/pdf", "data".getBytes());
        when(uploadFileUseCase.upload(
                org.mockito.ArgumentMatchers.eq("user@example.com"),
                org.mockito.ArgumentMatchers.eq("passport.pdf"),
                org.mockito.ArgumentMatchers.eq("application/pdf"),
                org.mockito.ArgumentMatchers.eq(4L),
                org.mockito.ArgumentMatchers.any(java.io.InputStream.class)
        )).thenReturn(metadata);

        ResponseEntity<FileMetadataResponse> response = fileController.upload("user@example.com", file);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("file-1", response.getBody().fileId());
    }

    @Test
    void upload_missingFile_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "passport.pdf", "application/pdf", new byte[0]);

        assertThrows(BadRequestException.class, () -> fileController.upload("user@example.com", file));
    }

    @Test
    void upload_streamFailure_throws() {
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return "passport.pdf";
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 4;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {1, 2};
            }

            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("boom");
            }

            @Override
            public void transferTo(java.io.File dest) {
                throw new UnsupportedOperationException();
            }
        };

        assertThrows(BadRequestException.class, () -> fileController.upload("user@example.com", file));
    }

    @Test
    void download_success() {
        DownloadFileUseCase.FileDownloadResult result =
                new DownloadFileUseCase.FileDownloadResult(metadata, new ByteArrayInputStream(new byte[] {1, 2}));
        when(downloadFileUseCase.download("file-1")).thenReturn(result);

        ResponseEntity<InputStreamResource> response = fileController.download("file-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertEquals(10L, response.getHeaders().getContentLength());
    }

    @Test
    void meta_success() {
        when(getFileMetaUseCase.getById("file-1")).thenReturn(metadata);

        ResponseEntity<FileMetadataResponse> response = fileController.meta("file-1");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("file-1", response.getBody().fileId());
    }

    @Test
    void delete_success() {
        doNothing().when(deleteFileUseCase).delete("file-1");

        ResponseEntity<Void> response = fileController.delete("file-1");

        assertEquals(204, response.getStatusCode().value());
    }
}
