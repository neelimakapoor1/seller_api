package com.goodlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.goodlife.model.ProductRegistry;

@Repository
public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, String>{

	@Query("select u from ProductRegistry u where u.asinId = ?1 and u.addedBy = ?2")
	public ProductRegistry findByAsinidAndAddedBy(String asinId, String addedBy);
	
	@Query("select u from ProductRegistry u where u.addedBy = ?1")
	public List<ProductRegistry> findByAddedBy(String addedBy);
	
}