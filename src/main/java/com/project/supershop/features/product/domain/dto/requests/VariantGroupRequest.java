package com.project.supershop.features.product.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VariantGroupRequest {
    private String name;
    private Boolean isPrimary;
    private Boolean isActive;
    private List<VariantRequest> variants;
}
