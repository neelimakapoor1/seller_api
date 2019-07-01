package com.goodlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goodlife.model.ProductKeywordRequest;

@Repository
public interface ProductKeywordRepository extends JpaRepository<ProductKeywordRequest, Integer>{

}