package com.project.supershop.features.product.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.product.domain.dto.requests.CategoryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "categoryImages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class CategoryImage extends BaseEntity {
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoryId")
    private Category category;

    public static CategoryImage createCategoryImage(String fileName, Category category){
        return CategoryImage.builder()
                .imageUrl(fileName)
                .category(category)
                .build();
    }
}
