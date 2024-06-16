package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.ProductVariant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends CrudRepository<ProductVariant, Integer> {
}
