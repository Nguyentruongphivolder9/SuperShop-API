package com.project.supershop.features.product.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String condition;
    private Boolean isVariant;
    private Boolean isActive;
    private List<ProductImagesResponse> productImages;
    private List<VariantGroupResponse> variantsGroup;
    private List<ProductVariantResponse> productVariants;
}
