package com.project.supershop.product.repositories;

import com.project.supershop.product.domain.entities.Variant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends CrudRepository<Variant, Integer> {
}
