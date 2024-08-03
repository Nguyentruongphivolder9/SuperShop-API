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
            "WHERE p.shop.id = :shopId AND p.id = :id AND p.isActive = :isActive")
    Optional<Product> findByProductIdAndIsActiveOfProductOfShop(@Param("id") UUID id, @Param("shopId") UUID shopId, @Param("isActive") boolean isActive);
    @Query("SELECT DISTINCT p FROM Product p " +
            "WHERE p.id = :id AND p.isActive = true")
    Optional<Product> findByProductIdOfProduct(@Param("id") UUID id);
    @Query("SELECT DISTINCT p FROM Product p " +
            "WHERE p.shop.id = :shopId AND p.id = :id")
    Optional<Product> findByProductIdOfProductOfShop(@Param("id") UUID id, @Param("shopId") UUID shopId);

    @Query(value = "SELECT DISTINCT p FROM Product p",
            countQuery = "SELECT COUNT(p) FROM Product p")
    Page<Product> findAll(Pageable pageable);
}
