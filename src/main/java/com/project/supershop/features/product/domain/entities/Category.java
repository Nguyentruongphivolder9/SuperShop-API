package com.project.supershop.features.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.CategoryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Category extends BaseEntity {
    private String name;
    private String parentId;
    private Boolean isActive;
    private Boolean isChild;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryImage> categoryImages;

    public static Category createCategory(CategoryRequest categoryRequest){
        return Category.builder()
                .name(categoryRequest.getName())
                .parentId(categoryRequest.getParentId())
                .isActive(Boolean.parseBoolean(categoryRequest.getIsActive()))
                .isChild(Boolean.parseBoolean(categoryRequest.getIsChild()))
                .build();
    }
}
