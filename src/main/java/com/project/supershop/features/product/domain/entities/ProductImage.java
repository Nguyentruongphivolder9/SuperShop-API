package com.project.supershop.features.product.domain.entities;


import com.project.supershop.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProductImage extends BaseEntity {
    private String name;
    private Boolean isPrimary;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "productId")
    private Product product;
}
