package com.airflights.file.presentation.controller;

import com.airflights.file.application.exception.BadRequestException;
import com.airflights.file.application.service.DeleteFileUseCase;
import com.airflights.file.application.service.DownloadFileUseCase;
import com.airflights.file.application.service.GetFileMetaUseCase;
import com.airflights.file.application.service.UploadFileUseCase;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.presentation.dto.FileMetadataResponse;
import com.airflights.file.presentation.mapper.FileMetadataPresentationMapper;
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
    private final FileMetadataPresentationMapper fileMetadataPresentationMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMetadataResponse> upload(
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail,
            @RequestPart("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        FileMetadata metadata = uploadFileUseCase.upload(
                userEmail,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                toStream(file)
        );
        return ResponseEntity.ok(fileMetadataPresentationMapper.toResponse(metadata));
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
        return ResponseEntity.ok(fileMetadataPresentationMapper.toResponse(metadata));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        deleteFileUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private java.io.InputStream toStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (java.io.IOException ex) {
            throw new BadRequestException("Failed to read file content", ex);
        }
    }
}
