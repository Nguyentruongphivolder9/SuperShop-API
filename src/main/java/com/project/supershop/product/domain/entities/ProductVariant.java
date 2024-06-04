package com.project.supershop.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.product.domain.dto.requests.ProductVariantRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "variant1Id")
    private Variant variant1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "variant2Id")
    private Variant variant2;

    public static ProductVariant createVariant(ProductVariantRequest productVariantRequest, Product product){
        return ProductVariant.builder()
                .price(productVariantRequest.getPrice())
                .stockQuantity(productVariantRequest.getStockQuantity())
                .product(product)
                .build();
    }
}
