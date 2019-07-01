package com.goodlife.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name="product_detail")
@Entity
public class ProductDetailRegistry {

	@Id
	@JsonProperty("detail_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="detail_id")
	private Integer detailId;
	
	@JsonIgnore
	@Column(name="product_id")
	private Integer productId;
	
	@JsonProperty("details")
	@Column(name="details")
	private String details;

	@Transient
	private boolean update;

	/**
	 * @return the details
	@ApiModelProperty(required = false, value = "")
	 */

	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	
	public boolean isUpdate() {
		return this.update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	/**
	 * @return the detailId
	@ApiModelProperty(required = false, value = "")
	 */
	
	public Integer getDetailId() {
		return detailId;
	}

	/**
	 * @param detailId the detailId to set
	 */
	public void setDetailId(Integer detailId) {
		this.detailId = detailId;
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

	public boolean isNew() {
		return !this.update;
	}


}
