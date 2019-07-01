package com.goodlife.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.goodlife.model.KeywordRankRequest;
import com.goodlife.model.Product;
import com.goodlife.model.ProductKeyword;
import com.goodlife.repository.KeywordRankRepository;
import com.goodlife.repository.ProductRepository;
import com.google.common.base.Strings;

public class KeywordTrackerService implements Runnable {
	Logger logger = Logger.getLogger(KeywordTrackerService.class);
	private static String name = "Keyword-tracker";

	private KeywordRankRepository keywordRankRepository;
	
	private ProductRepository productRepository;
	
	private ParseHubService parseHubService;
	
	public KeywordTrackerService (ProductRepository productRepository, KeywordRankRepository keywordRankRepository, 
			ParseHubService parseHubService) {
		this.productRepository = productRepository;
		this.keywordRankRepository = keywordRankRepository;
		this.parseHubService = parseHubService;
	}
	
	public void run(){
		logger.info(name + " started");
		try {
			while(true) {
				logger.info("Fetching all products for tracking-keywords");
				List<Product> allProducts = productRepository.findAll();
				for(Product product: allProducts) {
					trackKeywordsForProduct(product, null, false);
				}
				Thread.sleep(1000 * 60 * 60); //1 hour
			}
		}catch (Exception e) {
			logger.error(name + " interrupted", e);
		}
		logger.info(name + " exiting.");
	}
	
	public void trackKeywordsForProduct(Product product, String keywordFilter, Boolean refreshNow) {
		try {
			logger.info("Working on product " + product.getAsinId() + " added by " + product.getAddedBy() + " with refresh-interval " + product.getRefreshInterval() + " hours");
			Integer refreshIntervalInMilliSeconds = product.getRefreshInterval() * 60 * 60 * 1000;
			if (product.getKeywords() != null) {
				for(ProductKeyword keyword: product.getKeywords()) {
					if (keywordFilter == null || keyword.getKeyword().equals(keywordFilter)) {
						if (!Strings.isNullOrEmpty(keyword.getKeyword())) {
							logger.info("Working on keyword " + keyword.getKeyword());
							Date lastAddedDate = (keyword.getRank() != null)? keyword.getRankAddedDate(): null;
							//Fetch latest rank if you have refresh-interval has elapsed
							logger.info("Last rank added-date is " + ((lastAddedDate == null)? "null": lastAddedDate));
							if (refreshNow || lastAddedDate == null || ((new Date().getTime() - lastAddedDate.getTime()) > refreshIntervalInMilliSeconds)){
								logger.info("New keyword-rank will be added as the refresh-interval has elapsed");
								fetchFreshKeywordRankAndSave(keyword.getKeywordId(), product.getAsinId(), keyword.getKeyword());
							}
							Thread.sleep(1000 * 1); //1 second after each request
						}
					}
				}
			}
			else {
				logger.info("No keywords present for product " + product.getAsinId());
			}
		}
		catch(Exception e) {
			logger.error("Error while tracking keywords for product " + product.getAsinId(), e);
		}
	}
	
	/**
	 * Fetch fresh keyword-rank and save in db
	 * @param asinId
	 * @param keyword
	 * @throws Exception 
	 */
	private KeywordRankRequest fetchFreshKeywordRankAndSave(Integer keywordId, String asinId, String keyword) throws Exception {
		logger.debug("Fetching product-rank from ParseHub for product " + asinId + " and keyword " + keyword);
		//Integer rank = amazonProductApiClient.getProductRank(asinId, keyword);
		Map<String, Integer> keywordRankMap = parseHubService.getProductRankForKeywords(Arrays.asList(keyword), asinId);
		Integer organicRank = keywordRankMap.get(keyword + "_organic");
		Integer paidRank = keywordRankMap.get(keyword + "_paid");
		logger.info("Organic-rank and paid-rank for product " + asinId + " and keyword " + keyword + " is " + organicRank + " and " + paidRank);
		
		KeywordRankRequest keywordRank = new KeywordRankRequest();
		keywordRank.setKeywordId(keywordId);
		if (organicRank != null) {
			keywordRank.setRank(organicRank);
		}
		if (paidRank != null) {
			keywordRank.setPaidRank(paidRank);
		}
		if (organicRank != null || paidRank != null) {
			logger.info("Saving keyword-rank in db");
			keywordRankRepository.save(keywordRank);
		}
		
		return keywordRank;
	}
}
