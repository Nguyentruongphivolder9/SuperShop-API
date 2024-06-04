package com.project.supershop.product.repositories;

import com.project.supershop.product.domain.entities.VariantGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantGroupRepository extends CrudRepository<VariantGroup, Integer> {
}
