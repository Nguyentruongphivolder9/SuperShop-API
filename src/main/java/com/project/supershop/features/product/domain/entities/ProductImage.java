package com.project.supershop.features.product.domain.entities;


import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.ProductVariantRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "productImages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProductImage extends BaseEntity {
    private String imageUrl;
    private Boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    public static ProductImage createProductImage(String filename, Boolean isPrimary, Product product){
        return ProductImage.builder()
                .imageUrl(filename)
                .isPrimary(isPrimary)
                .product(product)
                .build();
    }
}
