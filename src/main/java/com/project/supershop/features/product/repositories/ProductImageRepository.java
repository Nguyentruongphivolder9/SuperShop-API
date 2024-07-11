package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.ProductImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductImageRepository extends CrudRepository<ProductImage, UUID> {
}
