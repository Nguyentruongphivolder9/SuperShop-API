package com.project.supershop.features.product.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.product.domain.dto.requests.ProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

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
    @Column(length = 5000)
    private String description;
    private String conditionProduct;
    private Integer sold;
    private Double ratingStart;
    private Boolean isVariant;
    private Boolean isActive;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VariantGroup> variantsGroup;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId")
    private Account shop;

    public static Product createProduct(ProductRequest productRequest, Category category, Account shop){
        return Product.builder()
                .shop(shop)
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .conditionProduct(productRequest.getConditionProduct())
                .description(productRequest.getDescription())
                .category(category)
                .sold(0)
                .ratingStart(0.0)
                .isVariant(productRequest.getIsVariant())
                .isActive(productRequest.getIsActive())
                .build();
    }
}
