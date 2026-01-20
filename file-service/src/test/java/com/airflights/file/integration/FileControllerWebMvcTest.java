package com.airflights.file.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.airflights.file.application.service.DeleteFileUseCase;
import com.airflights.file.application.service.DownloadFileUseCase;
import com.airflights.file.application.service.GetFileMetaUseCase;
import com.airflights.file.application.service.UploadFileUseCase;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.presentation.controller.FileController;
import com.airflights.file.presentation.exception.RestExceptionHandler;
import com.airflights.file.presentation.mapper.FileMetadataPresentationMapper;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
@Import({RestExceptionHandler.class, FileMetadataPresentationMapper.class})
class FileControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadFileUseCase uploadFileUseCase;

    @MockBean
    private DownloadFileUseCase downloadFileUseCase;

    @MockBean
    private GetFileMetaUseCase getFileMetaUseCase;

    @MockBean
    private DeleteFileUseCase deleteFileUseCase;

    @Test
    void upload_returnsMetadata() throws Exception {
        FileMetadata metadata = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                10L,
                LocalDateTime.now()
        );
        when(uploadFileUseCase.upload(eq("user@example.com"), eq("passport.pdf"), eq("application/pdf"), eq(10L), any()))
                .thenReturn(metadata);

        MockMultipartFile file = new MockMultipartFile("file", "passport.pdf", "application/pdf", new byte[10]);

        mockMvc.perform(multipart("/api/files")
                        .file(file)
                        .header("X-Auth-Email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value("file-1"))
                .andExpect(jsonPath("$.ownerId").value("user@example.com"));
    }

    @Test
    void meta_returnsMetadata() throws Exception {
        FileMetadata metadata = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                10L,
                LocalDateTime.now()
        );
        when(getFileMetaUseCase.getById("file-1")).thenReturn(metadata);

        mockMvc.perform(get("/api/files/file-1/meta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value("file-1"))
                .andExpect(jsonPath("$.bucket").value("airflights-files"));
    }

    @Test
    void download_returnsStream() throws Exception {
        FileMetadata metadata = new FileMetadata(
                "file-1",
                "user@example.com",
                "airflights-files",
                "user-uploads/user@example.com/file-1/passport.pdf",
                "application/pdf",
                2L,
                LocalDateTime.now()
        );
        DownloadFileUseCase.FileDownloadResult result =
                new DownloadFileUseCase.FileDownloadResult(metadata, new ByteArrayInputStream(new byte[] {1, 2}));
        when(downloadFileUseCase.download("file-1")).thenReturn(result);

        mockMvc.perform(get("/api/files/file-1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().longValue("Content-Length", 2L));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/files/file-1"))
                .andExpect(status().isNoContent());
    }
}
