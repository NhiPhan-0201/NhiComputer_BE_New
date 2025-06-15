package com.mshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mshop.entity.Product;

@Repository
public interface ProductResository extends JpaRepository<Product, Long>{
	@Query(value = "SELECT * FROM products WHERE category_id = ? and status = 1", nativeQuery = true)
	List<Product> findAllProductByCategoryId(Long id);

	@Query(value = "select * from products where status = 1", nativeQuery = true)
	List<Product> findAllStatusTrue();

	@Query(value = "select * from products where status = 1 and product_id = ?", nativeQuery = true)
	Product findByIdAndStatusTrue(Long id);

	@Query(value = "SELECT p.*, SUM(od.quantity) as total_sold " +
			"FROM products p " +
			"JOIN order_details od ON p.product_id = od.product_id " +
			"JOIN orders o ON od.order_id = o.id " +
			"WHERE o.status = 3 AND p.status = 1 " +
			"GROUP BY p.product_id " +
			"ORDER BY total_sold DESC " +
			"LIMIT ?1", nativeQuery = true)
	List<Product> findBestSellingProducts(int limit);

	List<Product> findByNameContainingIgnoreCase(String name);

}
