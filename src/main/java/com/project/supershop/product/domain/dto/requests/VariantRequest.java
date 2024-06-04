package com.project.supershop.product.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VariantRequest {
    private String name;
    private String imageUrl;
    private Boolean isActive;
}
