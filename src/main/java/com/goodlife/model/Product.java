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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Product {
	@Id
	@JsonProperty("product_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@JsonProperty("asin_id")
	@Column(name="asin_id")
	private String asinId;
	
	@JsonProperty("email1")
	@Column(name="email1")
	private String email1;
	
	@JsonProperty("email2")
	@Column(name="email2")
	private String email2;
	
	@JsonProperty("details")
	@OneToMany(
		 orphanRemoval = true,
	     cascade=CascadeType.ALL,
	     fetch = FetchType.EAGER
	)
	@JoinColumn(name = "product_id")
	private Set<ProductDetail> details = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(
	        orphanRemoval = true,
	        cascade=CascadeType.ALL,
	        fetch = FetchType.EAGER
	)
	@JoinColumn(name = "product_id")
	private Set<ProductKeyword> keywords = new HashSet<>();
	
	@JsonProperty("refresh_interval_in_hours")
	@Column(name="refresh_interval")
	private Integer refreshInterval = 24;
	
	@JsonProperty("receive_notifications")
	@Column(name="receive_notifications")
	private Boolean receiveNotifications = false;
	
	@JsonProperty("notification_interval")
	@Column(name="notification_interval")
	private Integer notificationInterval = 24;
	
	@JsonProperty("added_by")
	@Column(name="added_by")
	private String addedBy;

	@JsonProperty("added_date")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="added_date")
	private Date addedDate = null;
	

	/**
	 * @return the asinId
	@ApiModelProperty(required = true, value = "")
	 */
	@NotNull
	public String getAsinId() {
		return asinId;
	}

	/**
	 * @param asinId the asinId to set
	 */
	public void setAsinId(String asinId) {
		this.asinId = asinId;
	}

	/**
	 * @return the email1
	@ApiModelProperty(required = true, value = "")
	 */
	@NotNull
	public String getEmail1() {
		return email1;
	}

	/**
	 * @param email1 the email1 to set
	 */
	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	/**
	 * @return the email2
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getEmail2() {
		return email2;
	}

	/**
	 * @param email2 the email2 to set
	 */
	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	/**
	 * @return the refreshInterval in hours
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @param refreshInterval the refreshInterval to set in hours
	 */
	public void setRefreshInterval(Integer refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	/**
	 * @return the receiveNotifications
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Boolean getReceiveNotifications() {
		return receiveNotifications;
	}

	/**
	 * @param receiveNotifications the receiveNotifications to set
	 */
	public void setReceiveNotifications(Boolean receiveNotifications) {
		this.receiveNotifications = receiveNotifications;
	}

	/**
	 * @return the notificationInterval
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getNotificationInterval() {
		return notificationInterval;
	}

	/**
	 * @param notificationInterval the notificationInterval to set
	 */
	public void setNotificationInterval(Integer notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

	/**
	 * @return the addedBy
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getAddedBy() {
		return addedBy;
	}

	/**
	 * @param addedBy the addedBy to set
	 */
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
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
	 * @return the details
	@ApiModelProperty(required = false, value = "")
	 */
	@JsonIgnore
	public Set<ProductDetail> getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(Set<ProductDetail> details) {
		this.details = details;
	}

	/**
	 * @return the keywords
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Set<ProductKeyword> getKeywords() {
		return keywords;
	}
	
	/**
	 * @return the keyword_length
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getKeywordLength() {
		return keywords.size();
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(Set<ProductKeyword> keywords) {
		this.keywords = keywords;
	}

	public ProductDetail getDetail() {
		if (this.details.isEmpty()) {
			return null;
		}
		return this.details.iterator().next();
	}
	
	@Column(name="top_rank")
	public Integer getTopRank() {
		if (this.getKeywords() != null) {
			Integer topRank = 0;
			for (ProductKeyword keyword: this.getKeywords()) {
				if (keyword.getRank() != null) {
					topRank = Math.min(keyword.getRank(), topRank);
				}
			}
			return topRank;
		}
		return null;
	}
	
	@Column(name="top_rank_change")
	public Integer getTopRankChange() {
		return null;
	}

	

}
