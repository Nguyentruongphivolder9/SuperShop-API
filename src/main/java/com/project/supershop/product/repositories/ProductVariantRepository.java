package com.project.supershop.product.repositories;

import com.project.supershop.product.domain.entities.ProductVariant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends CrudRepository<ProductVariant, Integer> {
}
