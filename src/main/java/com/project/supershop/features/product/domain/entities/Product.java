package com.project.supershop.features.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Product extends BaseEntity {
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String description;
    private String conditionProduct;
    private Boolean isVariant;
    private Boolean isActive;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VariantGroup> variantsGroup;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants;

    public static Product createProduct(ProductRequest productRequest){
        return Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .isVariant(productRequest.getIsVariant())
                .isActive(productRequest.getIsActive())
                .build();
    }
}
