package com.mshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mshop.entity.CartDetail;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>{
	@Query(value = "select * from cart_details where cart_id = ?", nativeQuery = true)
	List<CartDetail> getByCartId(Long id);

	@Query(value = "select COUNT(cart_id) from cart_details where cart_id = ?", nativeQuery = true)
	Long getCount(Long id);

	// Thêm các phương thức mới
	@Query("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId AND cd.selected = true")
	List<CartDetail> findSelectedItemsByCartId(@Param("cartId") Long cartId);

	//@Query("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId AND cd.product.id = :productId")
	//List<CartDetail> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

}
