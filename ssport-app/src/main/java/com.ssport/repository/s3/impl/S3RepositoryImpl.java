package com.ssport.repository.s3.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.ssport.repository.s3.S3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class S3RepositoryImpl implements S3Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3RepositoryImpl.class);

    private final AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    public S3RepositoryImpl(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public String putObjectAndGetUrl(String key, MultipartFile file, Date expiration) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
            generatePresignedUrlRequest.withMethod(HttpMethod.GET);
            if (expiration != null) {
                generatePresignedUrlRequest.withExpiration(expiration);
            }

            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);

            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
            PutObjectResult result = amazonS3Client.putObject(request);

            return url.toString();
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return null;
    }

    @Override
    public String putByteArrayAndGetUrl(String key, byte[] data, String contentType, Date expiration) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(contentType);

        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
            generatePresignedUrlRequest.withMethod(HttpMethod.GET);
            if (expiration != null) {
                generatePresignedUrlRequest.withExpiration(expiration);
            }

            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);

            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
            PutObjectResult result = amazonS3Client.putObject(request);

            return url.toString();
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return null;
    }

    @Override
    public String updateAndGetUrl(String key, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
        generatePresignedUrlRequest.withMethod(HttpMethod.GET);
        if (expiration != null) {
            generatePresignedUrlRequest.withExpiration(expiration);
        }

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    @Override
    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void deleteObjects(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            deleteObjectsRequest.setKeys(keys.stream().map(DeleteObjectsRequest.KeyVersion::new).collect(Collectors.toList()));
            amazonS3Client.deleteObjects(deleteObjectsRequest);
        }
    }
}
