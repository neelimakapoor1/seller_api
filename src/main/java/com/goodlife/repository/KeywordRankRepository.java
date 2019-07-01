package com.goodlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goodlife.model.KeywordRankRequest;

@Repository
public interface KeywordRankRepository extends JpaRepository<KeywordRankRequest, Integer>{

}