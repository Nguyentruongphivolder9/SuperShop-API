package com.project.supershop.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadUtils {
    String uploadFile(MultipartFile imageFile, String brandImage, String imageName) throws IOException;
}
