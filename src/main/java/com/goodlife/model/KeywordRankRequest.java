package com.goodlife.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name="keyword_rank")
@Entity
public class KeywordRankRequest {
	@Id
	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rank_id")
	private Integer rankId;
	
	@JsonProperty("keyword_id")
	@Column(name="keyword_id")
	private Integer keywordId;
	
	@JsonProperty("rank")
	@Column(name="rank")
	private Integer rank;
	
	@JsonProperty("paid_rank")
	@Column(name="paid_rank")
	private Integer paidRank;
	

	/**
	 * @return the rankId
	@ApiModelProperty(required = true, value = "")
	 */
	public Integer getRankId() {
		return rankId;
	}

	/**
	 * @param rankId the rankId to set
	 */
	public void setRankId(Integer rankId) {
		this.rankId = rankId;
	}
	
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
	 * @return the rank
	@ApiModelProperty(required = true, value = "")
	 */
	@NotNull
	public Integer getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	/**
	 * @return the paidRank
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getPaidRank() {
		return paidRank;
	}

	/**
	 * @param paidRank the paidRank to set
	 */
	public void setPaidRank(Integer paidRank) {
		this.paidRank = paidRank;
	}
	
}
