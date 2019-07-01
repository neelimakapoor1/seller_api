package com.goodlife.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name="product_keyword")
@Entity
public class ProductKeyword {
	@Id
	@JsonProperty("keyword_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="keyword_id")
	private Integer keywordId;
	
	@JsonProperty("product_id")
	@Column(name="product_id")
	private Integer productId;
	
	@JsonProperty("keyword")
	@Column(name="keyword")
	private String keyword;
	
	@JsonProperty("added_date")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="added_date")
	private Date addedDate = null;
	
	@Transient
	@JsonProperty("history_chart_url")
	@Column(name="history_chart_url")
	private String historyChartUrl;
	
	@JsonProperty("rank_history")
	@OneToMany(
	        orphanRemoval = true,
	        cascade=CascadeType.ALL,
	        fetch = FetchType.EAGER
	)
	@JoinColumn(name = "keyword_id")
	private Set<KeywordRank> ranks = new HashSet<>();

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

	/**
	 * @return the addedDate
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Date getAddedDate() {
		return addedDate;
	}

	/**
	 * @param addedDate the addedDate to set
	 */
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	/**
	 * @return the rank-history
	@ApiModelProperty(required = false, value = "")
	 */
	public Set<KeywordRank> getRanks() {
		return ranks;
	}
	
	/**
	 * @return the most-recently added-rank
	@ApiModelProperty(required = false, value = "")
	 */
	@Column(name="rank")
	public Integer getRank() {
		if (this.ranks != null) {
			Date maxAddedDate = null;
			KeywordRank mostRecentRank = null;
			//Get maximum value of rank added-date
			for(KeywordRank keywordRank: this.ranks) {
				if (maxAddedDate == null || keywordRank.getAddedDate().after(maxAddedDate)) {
					maxAddedDate = keywordRank.getAddedDate();
					mostRecentRank = keywordRank;
				}
			}
			if (mostRecentRank != null) {
				return mostRecentRank.getRank();
			}
		}
		return null;
	}
	
	/**
	 * @return the most-recently added paid-rank
	@ApiModelProperty(required = false, value = "")
	 */
	@Column(name="paid_rank")
	public Integer getPaidRank() {
		if (this.ranks != null) {
			Date maxAddedDate = null;
			KeywordRank mostRecentRank = null;
			//Get maximum value of rank added-date
			for(KeywordRank keywordRank: this.ranks) {
				if (maxAddedDate == null || keywordRank.getAddedDate().after(maxAddedDate)) {
					maxAddedDate = keywordRank.getAddedDate();
					mostRecentRank = keywordRank;
				}
			}
			if (mostRecentRank != null) {
				return mostRecentRank.getPaidRank();
			}
		}
		return null;
	}
	

	/**
	 * @return the most-recently added-rank date
	@ApiModelProperty(required = false, value = "")
	 */
	@Column(name="rank_added_date")
	public Date getRankAddedDate() {
		if (this.ranks != null) {
			Date maxAddedDate = null;
			//Get maximum value of rank added-date
			for(KeywordRank keywordRank: this.ranks) {
				if (maxAddedDate == null || keywordRank.getAddedDate().after(maxAddedDate)) {
					maxAddedDate = keywordRank.getAddedDate();
				}
			}
			return maxAddedDate;
		}
		return null;
	}
	
	/**
	 * @return the change after last rank was added
	@ApiModelProperty(required = false, value = "")
	 */
	@Column(name="rank_change")
	public Integer getRankChange() {
		KeywordRank mostRecentRank = null;
		Date maxAddedDate = null;
		if (this.ranks != null) {
			//Get rank with maximum added-date
			for(KeywordRank keywordRank: this.ranks) {
				if (maxAddedDate == null || keywordRank.getAddedDate().after(maxAddedDate)) {
					maxAddedDate = keywordRank.getAddedDate();
					mostRecentRank = keywordRank;
				}
			}
		}
		
		
		KeywordRank secondMostRecentRank = null;
		Date secondMaxAddedDate = null;
		if (mostRecentRank!= null) {
			for(KeywordRank keywordRank: this.ranks) {
				//Get rank with second-maximum added-date
				if (secondMaxAddedDate == null || keywordRank.getAddedDate().after(secondMaxAddedDate)) {
					if(!keywordRank.getAddedDate().equals(maxAddedDate)) {
						secondMaxAddedDate = keywordRank.getAddedDate();
						secondMostRecentRank = keywordRank;
					}
				}
			}
		}
		
		if (secondMostRecentRank != null) {
			return mostRecentRank.getRank() - secondMostRecentRank.getRank();
		}
		return null;
	}

	/**
	 * @param ranks the ranks to set
	 */
	public void setRanks(Set<KeywordRank> ranks) {
		this.ranks = ranks;
	}

	/**
	 * @return the historyChartUrl
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getHistoryChartUrl() {
		return historyChartUrl;
	}

	/**
	 * @param historyChartUrl the historyChartUrl to set
	 */
	public void setHistoryChartUrl(String historyChartUrl) {
		this.historyChartUrl = historyChartUrl;
	}
	
	

}
