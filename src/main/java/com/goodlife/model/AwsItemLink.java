package com.goodlife.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemLink")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AwsItemLink {
	@XmlElement( name = "Description" )
	private String description;
	@XmlElement( name = "URL" )
	private String url;
	/**
	 * @return the description
	@ApiModelProperty(required = false, value = "")
	 */

	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the url
	@ApiModelProperty(required = false, value = "")
	 */

	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


}
