package com.ssport.repository.s3;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface S3Repository {
    String putObjectAndGetUrl(String key, MultipartFile file, Date expiration);

    String putByteArrayAndGetUrl(String key, byte[] data, String contentType, Date expiration);

    String updateAndGetUrl(String key, Date expiration);

    void deleteObject(String key);

    void deleteObjects(List<String> keys);
}
