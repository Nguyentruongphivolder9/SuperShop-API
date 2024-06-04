package com.project.supershop.product.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductVariantResponse {
    private Integer id;
    private Double price;
    private Integer stockQuantity;
    private VariantResponse variant1;
    private VariantResponse variant2;
}
