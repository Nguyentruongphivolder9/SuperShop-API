package com.project.supershop.product.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariantResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private Boolean isActive;
}
