package com.project.supershop.features.cart.repositories;

import com.project.supershop.features.cart.domain.entities.CartItem;
import com.project.supershop.features.product.domain.entities.Product;
import com.project.supershop.features.product.domain.entities.ProductInterest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query("SELECT DISTINCT ci FROM CartItem ci " +
            "WHERE ci.account.id = :accountId AND ci.product.id = :productId")
    Optional<CartItem> findCartItemByAccountIdAndProductId(@Param("accountId") UUID accountId, @Param("productId") UUID productId);

    @Query(value = "SELECT DISTINCT ci FROM CartItem ci WHERE ci.account.id = :accountId",
            countQuery = "SELECT COUNT(ci) FROM CartItem ci WHERE ci.account.id = :accountId")
    Page<CartItem> findListCartItemByAccountId(Pageable pageable, @Param("accountId") UUID accountId);
}
