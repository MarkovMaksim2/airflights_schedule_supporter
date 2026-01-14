package com.airflights.file.infrastructure.persistence;

import com.airflights.file.domain.model.FileMetadata;

public final class FileMapper {
    private FileMapper() {}

    public static FileEntity toEntity(FileMetadata metadata) {
        FileEntity entity = new FileEntity();
        entity.setFileId(metadata.fileId());
        entity.setOwnerId(metadata.ownerId());
        entity.setBucket(metadata.bucket());
        entity.setObjectKey(metadata.objectKey());
        entity.setContentType(metadata.contentType());
        entity.setSizeBytes(metadata.sizeBytes());
        entity.setCreatedAt(metadata.createdAt());
        return entity;
    }

    public static FileMetadata toDomain(FileEntity entity) {
        return new FileMetadata(
                entity.getFileId(),
                entity.getOwnerId(),
                entity.getBucket(),
                entity.getObjectKey(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getCreatedAt()
        );
    }
}
