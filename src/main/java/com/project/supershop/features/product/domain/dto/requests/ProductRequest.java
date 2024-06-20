package com.project.supershop.features.product.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String condition;
    private Boolean isVariant;
    private Boolean isActive;

    private List<ProductImagesRequest> productImagesRequests;
    private List<ProductVariantRequest> productVariants;
    private List<VariantGroupRequest> variantsGroup;
}
