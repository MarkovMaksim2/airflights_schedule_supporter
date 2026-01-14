package com.airflights.file.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileEntity, String> {
}
