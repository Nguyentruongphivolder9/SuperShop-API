package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
//    @EntityGraph(attributePaths = {"productImages", "variantsGroup", "productVariants"})
    @Query("SELECT DISTINCT p FROM Product p " +
//            "LEFT JOIN FETCH p.category c " +
//            "LEFT JOIN FETCH p.productImages pi " +
//            "LEFT JOIN FETCH p.variantsGroup vg " +
//            "LEFT JOIN FETCH vg.variants v " +
//            "LEFT JOIN FETCH p.productVariants pv " +
            "WHERE p.id = :id AND p.isActive = true")
    Optional<Product> findByProductId(@Param("id") UUID id);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c ",
//            "LEFT JOIN FETCH p.variantsGroup vg " +
//            "LEFT JOIN FETCH vg.variants v " +
//            "LEFT JOIN FETCH p.productImages pi " +
//            "LEFT JOIN FETCH p.productVariants pv ",
//            "LEFT JOIN FETCH pv.variants pvv",
            countQuery = "SELECT COUNT(p) FROM Product p")
    Page<Product> findAll(Pageable pageable);
}
