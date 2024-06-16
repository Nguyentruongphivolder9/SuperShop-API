package com.project.supershop.features.product.domain.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    @NotNull(message = "Product name is require")
    @NotBlank(message = "Product name can't blank")
    private String name;
    private Double price;
    private Integer stockQuantity;

    @NotNull(message = "Status variant is require")
    private Boolean isVariant;

    @NotNull(message = "Status active is require")
    private Boolean isActive;

    private List<ProductVariantRequest> productVariants;
    private List<VariantGroupRequest> variantsGroup;
}
