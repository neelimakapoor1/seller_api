package com.goodlife.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AwsItem {
	
	@XmlElement( name = "ASIN" )
	private String asin;
	
	@XmlElement( name = "ParentASIN" )
	private String parentAsin;
	
	@XmlElement( name = "DetailPageURL" )
	private String detailPageUrl;
	
	@XmlElement( name = "ItemLinks" )
	private List<AwsItemLink> itemLinks;
	
	@XmlElement( name = "ItemAttributes" )
	private AwsItemAttributes itemAttributes;

	/**
	 * @return the asin
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getAsin() {
		return asin;
	}

	/**
	 * @param asin the asin to set
	 */
	public void setAsin(String asin) {
		this.asin = asin;
	}

	/**
	 * @return the parentAsin
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getParentAsin() {
		return parentAsin;
	}

	/**
	 * @param parentAsin the parentAsin to set
	 */
	public void setParentAsin(String parentAsin) {
		this.parentAsin = parentAsin;
	}

	/**
	 * @return the detailPageUrl
	@ApiModelProperty(required = false, value = "")
	 */
	
	public String getDetailPageUrl() {
		return detailPageUrl;
	}

	/**
	 * @param detailPageUrl the detailPageUrl to set
	 */
	public void setDetailPageUrl(String detailPageUrl) {
		this.detailPageUrl = detailPageUrl;
	}

	/**
	 * @return the itemLinks
	@ApiModelProperty(required = false, value = "")
	 */
	
	public List<AwsItemLink> getItemLinks() {
		return itemLinks;
	}

	/**
	 * @param itemLinks the itemLinks to set
	 */
	public void setItemLinks(List<AwsItemLink> itemLinks) {
		this.itemLinks = itemLinks;
	}

	/**
	 * @return the itemAttributes
	@ApiModelProperty(required = false, value = "")
	 */
	
	public AwsItemAttributes getItemAttributes() {
		return itemAttributes;
	}

	/**
	 * @param itemAttributes the itemAttributes to set
	 */
	public void setItemAttributes(AwsItemAttributes itemAttributes) {
		this.itemAttributes = itemAttributes;
	}

	
	
}
