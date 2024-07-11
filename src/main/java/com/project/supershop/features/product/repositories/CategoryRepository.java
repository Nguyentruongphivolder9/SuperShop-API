package com.project.supershop.features.product.repositories;

import com.project.supershop.features.product.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.categoryImages WHERE c.parentId = :parentId AND c.isChild = :isChild")
    List<Category> findAllCategoriesWithImagesByParentIdAndIsChild(@Param("parentId") String parentId, @Param("isChild") Boolean isChild);
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.categoryImages WHERE c.parentId = :parentId")
    List<Category> findAllCategoriesWithImagesByParentId(@Param("parentId") String parentId);
}
