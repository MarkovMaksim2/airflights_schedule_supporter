package com.airflights.file.domain.port;

import java.io.InputStream;

public interface ObjectStorage {
    void putObject(String bucket, String objectKey, InputStream content, long size, String contentType);
    InputStream getObject(String bucket, String objectKey);
    void deleteObject(String bucket, String objectKey);
}
