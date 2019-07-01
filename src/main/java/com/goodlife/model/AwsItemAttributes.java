package com.goodlife.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemAttributes")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AwsItemAttributes {

	@XmlElement( name = "Manufacturer" )
	private String manufacturer;

	@XmlElement( name = "ProductGroup" )
	private String productGroup;

	@XmlElement( name = "Title" )
	private String title;

	/**
	 * @return the manufacturer
	@ApiModelProperty(required = false, value = "")
	 */

	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @param manufacturer the manufacturer to set
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * @return the productGroup
	@ApiModelProperty(required = false, value = "")
	 */

	public String getProductGroup() {
		return productGroup;
	}

	/**
	 * @param productGroup the productGroup to set
	 */
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	/**
	 * @return the title
	@ApiModelProperty(required = false, value = "")
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


}
