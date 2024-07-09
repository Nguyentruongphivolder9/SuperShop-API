package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.Variant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VariantRepository extends CrudRepository<Variant, UUID> {
}
