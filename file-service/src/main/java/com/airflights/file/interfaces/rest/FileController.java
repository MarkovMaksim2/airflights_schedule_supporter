package com.airflights.file.interfaces.rest;

import com.airflights.file.application.usecase.DeleteFileUseCase;
import com.airflights.file.application.usecase.DownloadFileUseCase;
import com.airflights.file.application.usecase.GetFileMetaUseCase;
import com.airflights.file.application.usecase.UploadFileUseCase;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.interfaces.rest.dto.FileMetadataResponse;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final DownloadFileUseCase downloadFileUseCase;
    private final GetFileMetaUseCase getFileMetaUseCase;
    private final DeleteFileUseCase deleteFileUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMetadataResponse> upload(
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail,
            @RequestPart("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "File is required"
            );
        }
        FileMetadata metadata = uploadFileUseCase.upload(
                userEmail,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                toStream(file)
        );
        return ResponseEntity.ok(toResponse(metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") String id) {
        DownloadFileUseCase.FileDownloadResult result = downloadFileUseCase.download(id);
        FileMetadata metadata = result.metadata();
        InputStreamResource resource = new InputStreamResource(result.content());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.fileId() + "\"")
                .contentType(MediaType.parseMediaType(metadata.contentType()))
                .contentLength(metadata.sizeBytes())
                .body(resource);
    }

    @GetMapping("/{id}/meta")
    public ResponseEntity<FileMetadataResponse> meta(@PathVariable("id") String id) {
        FileMetadata metadata = getFileMetaUseCase.getById(id);
        return ResponseEntity.ok(toResponse(metadata));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteFileUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private FileMetadataResponse toResponse(FileMetadata metadata) {
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

    private java.io.InputStream toStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (java.io.IOException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Failed to read file content",
                    ex
            );
        }
    }
}
