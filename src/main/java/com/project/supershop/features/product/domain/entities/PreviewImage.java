package com.project.supershop.features.product.domain.entities;

import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "previewImage")
public class PreviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String imageUrl;

    public static PreviewImage createProduct(String preImageUrl){
        return PreviewImage.builder()
                .imageUrl(preImageUrl)
                .build();
    }
}
