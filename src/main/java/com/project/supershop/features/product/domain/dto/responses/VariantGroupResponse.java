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
public class VariantGroupResponse {
    private Integer id;
    private String name;
    private Boolean isPrimary;
    private Boolean isActive;
    private List<VariantResponse> variants;
}
