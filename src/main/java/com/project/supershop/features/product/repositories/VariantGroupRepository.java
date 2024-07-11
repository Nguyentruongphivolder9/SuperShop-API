package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.VariantGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VariantGroupRepository extends CrudRepository<VariantGroup, UUID> {
}
