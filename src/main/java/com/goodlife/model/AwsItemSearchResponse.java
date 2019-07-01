package com.goodlife.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemSearchResponse")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AwsItemSearchResponse {
	
	@XmlElement( name = "TotalResults" )
	private Integer totalResults;
	
	@XmlElement( name = "TotalPages" )
	private Integer totalPages;
	
	@XmlElement( name = "MoreSearchResultsUrl" )
	private String moreSearchResultsUrl;
	
	@XmlElement( name = "Item" )
	private AwsItem item;
}
