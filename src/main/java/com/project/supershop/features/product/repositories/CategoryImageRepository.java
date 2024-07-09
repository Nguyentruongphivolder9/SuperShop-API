package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, UUID> {
}