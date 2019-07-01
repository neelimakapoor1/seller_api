package com.goodlife.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.goodlife.exception.BadRequestException;
import com.goodlife.exception.InternalException;
import com.goodlife.exception.NotFoundException;
import com.goodlife.exception.UnauthorizedException;

@Service
public class AmazonProductApiClient {
	Logger logger = Logger.getLogger(AmazonMWSClient.class);

	@Value("${aws.product.api.base.url}")
	private String serviceURL;

	@Value("${productapi.access.key}")
	private String awsAccessKey;

	@Value("${aws.associate.tag}")
	private String associateTag;

	@Value("${productapi.secret.key}")
	private String awsSecretKey;

	
	public Integer getProductRank(String asinId, String keyword) throws Exception {
		logger.info("Getting product-details for " + asinId + " from Amazon Product Advertising API");
	
        try {
            // Call the service.
	        	TreeMap<String, String> requestParams = new TreeMap<>();
	        	requestParams.put("Keywords", "presenter");
        	    String responseXml = this.postRequestToAms("GET", "/onca/xml", "ItemSearch", requestParams);
            logger.info(responseXml);
//            if  (response.getListMatchingProductsResult().getProducts().getProduct().isEmpty()) {
//	    			throw new NotFoundException("No products found with keyword " + keyword);
//	    		}
//            Integer rank = 0;
//            for(Product product: response.getListMatchingProductsResult().getProducts().getProduct()) {
//            		rank++;
//            		if (product.getIdentifiers().getMarketplaceASIN().getASIN().equals(asinId)) {
//            			return rank;
//            		}
//            }
            return null; //Not more than 10 returned
        } catch (Exception ex) {
            // Exception properties are important for diagnostics.
        		logger.error(ex);
            throw ex;
        }
	}
	
	private String postRequestToAms(String method, String HttpRequestUrl, String operation, TreeMap<String, String> requestParams) throws Exception {
		//Calculate queryString
		TreeMap<String, String> params = new TreeMap<>();
		params.put("AWSAccessKeyId", awsAccessKey);
		params.put("Service", "AWSECommerceService");
		params.put("AssociateTag", associateTag);
		params.put("Operation", operation);
		//params.put("SignatureMethod", "HmacSHA256");
		//params.put("SignatureVersion", "2");
		params.put("Timestamp", getCurrentTimestampInIso8601Format());
		params.put("Version", "2013-08-01");
		if (requestParams != null) {
			params.putAll(requestParams);
		}
		String queryString = buildQueryString(params, (method.equals("GET")?true: false));

		//Append signature to body
		String signature =  buildSignature(method + "\n" +
				serviceURL + "\n" +
				HttpRequestUrl + "\n" +
				buildQueryString(params, true));
		queryString = queryString.concat(String.format("&Signature=%s", URLEncoder.encode(signature, "UTF-8")));
		

		//Send request
		String url = "https://".concat(serviceURL.concat(HttpRequestUrl));
		logger.info("Sending " + method + " request with query-string " + queryString + " and url " + url);
		if (method.equals("GET")) {
			url = url.concat("?").concat(queryString);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (method.equals("POST")) {
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		}
		conn.setRequestProperty("Accept", "text/xml");
		conn.setRequestProperty("X-Amazon-User-Agent", "GoodlifeEcommerce/build0.1 (Language=Java; Host=localhost)");
		conn.setRequestMethod(method);
		conn.setDoOutput(true);

		//Send body in case of post
		if (method.equals("POST")) {
			//Send message content.
			logger.info("Adding body");
			OutputStream os = conn.getOutputStream();
			os.write(queryString.getBytes());
			os.flush();
			os.close();
		}

		//Read response.
		logger.info("response_code for " + url + " is ::" + conn.getResponseCode());
		if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
			InputStream inputStream = conn.getInputStream();
			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(inputStream, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			logger.info("response_body for " + url + " is ::" + out.toString());
			return out.toString();
		} else if (conn.getResponseCode() == 204) {
			return null;
		} else if (conn.getResponseCode() == 401) {
			throw new UnauthorizedException("User is not authorized");
		} else if (conn.getResponseCode() == 404) {
			throw new NotFoundException("No results found");
		} else {
			InputStream inputStream = conn.getErrorStream();
			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(inputStream, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			logger.debug("response_body for " + url + " is ::" + out.toString());
			if (conn.getResponseCode() == 400) {
				throw new BadRequestException(
						"Got invalid response from MWS " + conn.getResponseCode() + " and error " + out.toString());
			}
			else {
				throw new InternalException(
						"Got invalid response from MWS " + conn.getResponseCode() + " and error " + out.toString());
			}
		}
	}

	private String buildQueryString(TreeMap<String, String> params, Boolean urlEncode) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (String paramName: params.keySet()) {
			if (urlEncode) {
				sb.append(URLEncoder.encode(paramName, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(params.get(paramName), "UTF-8"));
				sb.append("&");
			}
			else {
				sb.append(paramName);
				sb.append("=");
				sb.append(params.get(paramName));
				sb.append("&");
			}
		}
		return sb.substring(0, sb.length() - 1); //remove & in end
	}


	private String buildSignature(String message) throws InvalidKeyException, NoSuchAlgorithmException {	
		String signature = CryptoService.hamcSha256(awsSecretKey, message);
		logger.info("signature for message " + message + " secret-key " + awsSecretKey + " is " + signature);
		return signature;
	}

	private String getCurrentTimestampInIso8601Format() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");  //Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		return df.format(new Date());
	}
}
