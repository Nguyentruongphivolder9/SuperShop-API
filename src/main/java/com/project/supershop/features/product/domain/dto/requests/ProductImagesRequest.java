package com.project.supershop.features.product.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductImagesRequest {
    private String id;
    private MultipartFile imageFile;
}
