package com.project.supershop.features.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.ProductVariantRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "productVariants")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProductVariant extends BaseEntity {
    private Double price;
    private Integer stockQuantity;

    @ManyToOne
    private Product product;

    @ManyToOne
    @JoinColumn(name = "variant1Id")
    private Variant variant1;

    @ManyToOne
    @JoinColumn(name = "variant2Id")
    private Variant variant2;

    public static ProductVariant createProductVariant(ProductVariantRequest productVariantRequest, Product product, Variant variant1, Variant variant2){
        return ProductVariant.builder()
                .price(productVariantRequest.getPrice())
                .stockQuantity(productVariantRequest.getStockQuantity())
                .product(product)
                .variant1(variant1)
                .variant2(variant2)
                .build();
    }
}
