package com.project.supershop.features.product.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductVariantRequest {
    private Double price;
    private Integer stockQuantity;
    private String variantsGroup1;
    private String variant1;
    private String variantsGroup2;
    private String variant2;
}
