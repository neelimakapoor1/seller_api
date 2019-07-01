package com.goodlife.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name="product_keyword")
@Entity
public class ProductKeywordRequest {
	@Id
	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_id")
	private Integer keywordId;
	
	@JsonIgnore
	@Column(name="product_id")
	private Integer productId;
	
	@JsonProperty("keyword")
	@Column(name="keyword")
	private String keyword;

	/**
	 * @return the keywordId
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getKeywordId() {
		return keywordId;
	}

	/**
	 * @param keywordId the keywordId to set
	 */
	public void setKeywordId(Integer keywordId) {
		this.keywordId = keywordId;
	}

	/**
	 * @return the productId
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	/**
	 * @return the keyword
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
