package com.goodlife.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import com.amazonaws.mws.products.model.GetMatchingProductForIdResponse;
import com.goodlife.exception.BadRequestException;
import com.goodlife.model.KeywordRank;
import com.goodlife.model.Product;
import com.goodlife.model.ProductDetailRegistry;
import com.goodlife.model.ProductKeyword;
import com.goodlife.model.ProductKeywordRequest;
import com.goodlife.model.ProductRegistry;
import com.goodlife.repository.KeywordRankRepository;
import com.goodlife.repository.ProductDetailRegistryRepository;
import com.goodlife.repository.ProductKeywordRepository;
import com.goodlife.repository.ProductRegistryRepository;
import com.goodlife.repository.ProductRepository;

@Service
public class ProductService {
	Logger logger = Logger.getLogger(ProductService.class);

	@Autowired
	private ProductRegistryRepository productRegistryRepository;
	
	@Autowired
	private ProductKeywordRepository productKeywordRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductDetailRegistryRepository productDetailRegistryRepository;
	
	@Autowired
	private AmazonMWSClient amazonMwsClient;
	
	@Autowired
	private ParseHubService parseHubService;
	
	@Autowired
	private KeywordRankRepository keywordRankRepository;
	
	private static KeywordTrackerService keywordTrackerService;
	
	@Value("${upload.url}")
	private String uploadUrl;
	
	@Value("${upload.path}")
	private String uploadPath;
	
	SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm");

	
	public void registerProduct(ProductRegistry productRegistry) {
		
		startKeywordTrackerService();
		
		ProductRegistry existingProductRegistry = null;
		try {
			existingProductRegistry = productRegistryRepository.findByAsinidAndAddedBy(productRegistry.getAsinId(), productRegistry.getAddedBy());
			if (existingProductRegistry != null) {
				//raise exception
				throw new BadRequestException("Product " + productRegistry.getAsinId() + " is already registered with us");
			}
		}
		catch(EntityNotFoundException | JpaObjectRetrievalFailureException e) {
			logger.info("Product not already registered");
		}
		
		logger.info("Getting product detail");
		GetMatchingProductForIdResponse awsProductInfo = amazonMwsClient.getProductById(productRegistry.getAsinId());
		
		logger.info("Registering product " + productRegistry.getAsinId() + " with keywords " + productRegistry.getKeywords().size());
		productRegistry = productRegistryRepository.save(productRegistry);
		
		logger.info("Saving product detail");
		ProductDetailRegistry productDetailRegistry = new ProductDetailRegistry();
		productDetailRegistry.setProductId(productRegistry.getProductId());
		productDetailRegistry.setDetails("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + awsProductInfo.toXML());
		productDetailRegistryRepository.save(productDetailRegistry);
		
		//logger.info("Start tracking keywords for product");
		//Product product = productRepository.findOne(productRegistry.getProductId());
		//keywordTrackerService.trackKeywordsForProduct(product, null, true);
	}
	
	public List<Product> getRegisteredProducts(String addedBy, Boolean refresh) {

		startKeywordTrackerService();
		
		List<Product> productList = productRepository.findByAddedBy(addedBy);
		if (refresh) {
			for (Product product: productList) {
				//logger.debug("Refreshing product ranks from Amazon");
				//keywordTrackerService.trackKeywordsForProduct(product, null, true);
				
				logger.debug("Fetching product-info from MWS for product " + product.getAsinId());
				GetMatchingProductForIdResponse awsProductInfo = amazonMwsClient.getProductById(product.getAsinId());
				ProductDetailRegistry productDetail = new ProductDetailRegistry();
				productDetail.setProductId(product.getProductId());
				productDetail.setDetails("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + awsProductInfo.toXML());
				productDetailRegistryRepository.save(productDetail);
			}
			productList = productRepository.findByAddedBy(addedBy);
		}
		return productList;
	}
	
	public Set<ProductKeyword> getProductKeywords(Integer productId) throws IOException {
		
		startKeywordTrackerService();
		
		Product product = productRepository.findOne(productId);
		Set<ProductKeyword> keywords = product.getKeywords();
		for(ProductKeyword keyword: keywords) {
			if (keyword != null) {
				Set<KeywordRank> ranks = keyword.getRanks();
				if (ranks != null) {
					TreeMap<Date, Integer> dateToRankMap = new TreeMap<>();
					for(KeywordRank rank: ranks) {
						if (rank.getRank() != null && rank.getAddedDate() != null) {
							dateToRankMap.put(rank.getAddedDate(), rank.getRank());
						}
					}
					if (!dateToRankMap.isEmpty()) {					
						logger.info("Building chart for keyword " + keyword.getKeyword() + " for product " + productId);
						String chartUrl = createLineChart(productId + "_" + keyword, dateToRankMap, "Time", "Ranks", "Rank History");
						logger.info("Rank-history chart-url for keyword " + keyword.getKeyword() + " is " + chartUrl);
						keyword.setHistoryChartUrl(chartUrl);
					}
				}
			}
		}
		return keywords;
	}
	
	public void addProductKeyword(Integer productId, ProductKeywordRequest productKeywordRequest) {		
		startKeywordTrackerService();
		
		productKeywordRequest.setProductId(productId);
		productKeywordRepository.save(productKeywordRequest);
		
		//logger.info("Start tracking keywords for product");
		//Product product = productRepository.findOne(productId);
		//keywordTrackerService.trackKeywordsForProduct(product, productKeywordRequest.getKeyword(), true);
	}
	
	public void deleteKeyword(Integer keywordId) {
		productKeywordRepository.delete(keywordId);
	}
	
	private void startKeywordTrackerService() {
		if (keywordTrackerService == null) {
			logger.info("Start tracking keywords service");
			keywordTrackerService = new KeywordTrackerService (productRepository, keywordRankRepository, parseHubService);
			Thread keywordTrackerThread = new Thread(keywordTrackerService);
			keywordTrackerThread.start();	
		}
	}

   private String createLineChart(String uniqueId, TreeMap<Date, Integer> dateToRanksMap, String xValueLabel, String yValueLabel, String chartTitle) throws IOException  {
      DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
      for (Date date: dateToRanksMap.keySet()) {
    	    if(line_chart_dataset.getRowCount() < 20) {
    	    		line_chart_dataset.addValue(dateToRanksMap.get(date) , yValueLabel ,  format.format(date));
    	    }
      }

      JFreeChart lineChartObject = ChartFactory.createLineChart(
         chartTitle, xValueLabel,
         yValueLabel,
         line_chart_dataset,PlotOrientation.VERTICAL, 
         false, true, false);

      int width = 640;    /* Width of the image */
      int height = 480;   /* Height of the image */ 
      String fileName = uniqueId + "_" + (new Date().getTime()) + ".png";
      File lineChart = new File(uploadPath + "/" + fileName );
      ChartUtilities.saveChartAsPNG(lineChart ,lineChartObject, width ,height);
      
      return uploadUrl + "/" + fileName;
   }
	

}
