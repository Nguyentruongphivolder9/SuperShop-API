package com.project.supershop.features.product.domain.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String conditionProduct;
    private String categoryId;
    private String description;
    private Boolean isVariant;
    private Boolean isActive;

    private List<ProductImagesRequest> productImages;
    private List<ProductVariantRequest> productVariants;
    private List<VariantGroupRequest> variantsGroup;
}
