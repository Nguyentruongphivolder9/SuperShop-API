package com.project.supershop.features.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.VariantGroupRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "variantsGroup")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class VariantGroup extends BaseEntity {
    private String name;
    private Boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @OneToMany(mappedBy = "variantGroup", fetch = FetchType.LAZY)
    private List<Variant> variants;

    public static VariantGroup createVariantGroup(VariantGroupRequest groupRequest, Product product){
        return VariantGroup.builder()
                .name(groupRequest.getName())
                .isPrimary(groupRequest.getIsPrimary())
                .product(product)
                .build();
    }

}
