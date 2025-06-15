package com.mshop.repository;

import com.mshop.entity.Product;
import com.mshop.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    @Query("SELECT p FROM Product p WHERE p.subcategory.subcategoryId = :subcategoryId")
    List<Product> findAllProductBySubcategoryId(@Param("subcategoryId") Long subcategoryId);

} 