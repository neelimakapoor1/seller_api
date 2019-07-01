package com.goodlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goodlife.model.ProductDetailRegistry;

@Repository
public interface ProductDetailRegistryRepository extends JpaRepository<ProductDetailRegistry, String>{
//	@Query("select u from Product u where u.productId = ?1")
//	public ProductDetail findByProductId(String productId);
}