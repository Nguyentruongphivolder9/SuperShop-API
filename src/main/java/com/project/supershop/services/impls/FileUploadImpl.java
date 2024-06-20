package com.project.supershop.services.impls;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.supershop.services.FileUploadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileUploadImpl implements FileUploadUtils {
    @Value("amazon.bucket-name")
    private String bucketName;

    @Value("amazon.endpoint-url")
    private String url;

    private AmazonS3 amazonS3;

    public FileUploadImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFile(MultipartFile imageFile, String brandImage, String imageName) throws IOException {
        if (!Arrays.asList("image/png", "image/jpeg").contains(imageFile.getContentType())) {
            throw new IllegalStateException("File uploaded is not an image");
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", imageFile.getContentType());
        metadata.put("Content-Length", String.valueOf(imageFile.getSize()));
        String path = String.format("%s/%s", bucketName, brandImage);
        String fileName = String.format("%s-%s", imageName, UUID.randomUUID());
        String contentType = imageFile.getContentType();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        Optional.of(metadata).ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        objectMetadata.setContentType(contentType);
        try {
            amazonS3.putObject(path, fileName, imageFile.getInputStream(), objectMetadata);
            return url + "/" + path + "/" + fileName;
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }
}
