package com.project.supershop.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.product.domain.dto.requests.VariantGroupRequest;
import com.project.supershop.product.domain.dto.requests.VariantRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "variants")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Variant extends BaseEntity {
    private String name;
    private String imageUrl;
    private Boolean isActive;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "variantGroupId")
    private VariantGroup variantGroup;

    public static Variant createVariant(VariantRequest variantRequest, VariantGroup variantGroup){
        return Variant.builder()
                .name(variantRequest.getName())
                .imageUrl(variantRequest.getImageUrl())
                .isActive(variantRequest.getIsActive())
                .variantGroup(variantGroup)
                .build();
    }
}
