package com.goodlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.goodlife.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	@Query("select u from Product u where u.asinId = ?1 and u.addedBy = ?2")
	public Product findByAsinidAndAddedBy(String asinId, String addedBy);
	
	@Query("select u from Product u where u.addedBy = ?1")
	public List<Product> findByAddedBy(String addedBy);
	
}