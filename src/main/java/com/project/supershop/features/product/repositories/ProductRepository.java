package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
