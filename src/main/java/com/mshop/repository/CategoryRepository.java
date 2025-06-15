package com.mshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mshop.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	List<Category> findByStatusTrue();
	@Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.categoryName = :name AND c.status = true")
	boolean existsByNameAndStatusTrue(@Param("name") String name);
}
