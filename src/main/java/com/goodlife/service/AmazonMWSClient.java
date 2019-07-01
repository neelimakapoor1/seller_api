package com.goodlife.service;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.mws.products.MarketplaceWebServiceProductsAsyncClient;
import com.amazonaws.mws.products.MarketplaceWebServiceProductsClient;
import com.amazonaws.mws.products.MarketplaceWebServiceProductsConfig;
import com.amazonaws.mws.products.MarketplaceWebServiceProductsException;
import com.amazonaws.mws.products.model.GetMatchingProductForIdRequest;
import com.amazonaws.mws.products.model.GetMatchingProductForIdResponse;
import com.amazonaws.mws.products.model.IdListType;
import com.amazonaws.mws.products.model.ListMatchingProductsRequest;
import com.amazonaws.mws.products.model.ListMatchingProductsResponse;
import com.amazonaws.mws.products.model.Product;
import com.amazonaws.mws.products.model.ResponseHeaderMetadata;
import com.goodlife.exception.NotFoundException;


@Service
public class AmazonMWSClient {
	Logger logger = Logger.getLogger(AmazonMWSClient.class);

	@Value("${mws.base.url}")
	private String serviceURL;

	@Value("${aws.access.key}")
	private String awsAccessKey;
	
	@Value("${mws.seller.id}")
	private String sellerId;
	
	@Value("${mws.marketplace.id}")
	private String marketPlaceId;
	
	@Value("${ams.auth.token}")
	private String mwsAuthToken;
	
	@Value("${mws.secret.key}")
	private String mwsSecretKey;

	 /** The client, lazy initialized. Async client is also a sync client. */
    private static MarketplaceWebServiceProductsAsyncClient client = null;
	
	public GetMatchingProductForIdResponse getProductById(String asinId)  {
		logger.info("Getting product-details for " + asinId + " from MWS");
		MarketplaceWebServiceProductsClient client = getAsyncClient(serviceURL, awsAccessKey, mwsSecretKey);

        // Create a request.
        GetMatchingProductForIdRequest request = new GetMatchingProductForIdRequest();
        request.setSellerId(sellerId);
        request.setMWSAuthToken(mwsAuthToken);
        request.setMarketplaceId(marketPlaceId);
        request.setIdType("ASIN");
        IdListType idList = new IdListType();
        idList.setId(Arrays.asList(asinId));
        request.setIdList(idList);
        
        try {
            // Call the service.
            GetMatchingProductForIdResponse response = client.getMatchingProductForId(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.info("Response:");
            logger.info("RequestId: "+rhmd.getRequestId());
            logger.info("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.info(responseXml);
            if  (response.getGetMatchingProductForIdResult().isEmpty() || response.getGetMatchingProductForIdResult().get(0).getProducts().getProduct().isEmpty()) {
	    			throw new NotFoundException("No products found");
	    		}
            return response;
        } catch (MarketplaceWebServiceProductsException ex) {
            // Exception properties are important for diagnostics.
            logger.info("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
                logger.info("RequestId: "+rhmd.getRequestId());
                logger.info("Timestamp: "+rhmd.getTimestamp());
            }
            logger.info("Message: "+ex.getMessage());
            logger.info("StatusCode: "+ex.getStatusCode());
            logger.info("ErrorCode: "+ex.getErrorCode());
            logger.info("ErrorType: "+ex.getErrorType());
            throw ex;
        }
	}
	
	public Integer getProductRank(String asinId, String keyword) {
		logger.info("Getting product-details for " + asinId + " from MWS");
		MarketplaceWebServiceProductsClient client = getAsyncClient(serviceURL, awsAccessKey, mwsSecretKey);

        // Create a request.
		ListMatchingProductsRequest request = new ListMatchingProductsRequest();
		request.setSellerId(sellerId);
        request.setMWSAuthToken(mwsAuthToken);
        request.setMarketplaceId(marketPlaceId);
	    request.setQuery(keyword);
        
        try {
            // Call the service.
            ListMatchingProductsResponse response = client.listMatchingProducts(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.info("Response:");
            logger.info("RequestId: "+rhmd.getRequestId());
            logger.info("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.info(responseXml);
            if  (response.getListMatchingProductsResult().getProducts().getProduct().isEmpty()) {
	    			throw new NotFoundException("No products found with keyword " + keyword);
	    		}
            Integer rank = 0;
            for(Product product: response.getListMatchingProductsResult().getProducts().getProduct()) {
            		rank++;
            		if (product.getIdentifiers().getMarketplaceASIN().getASIN().equals(asinId)) {
            			return rank;
            		}
            }
            return null; //Not more than 10 returned
        } catch (MarketplaceWebServiceProductsException ex) {
            // Exception properties are important for diagnostics.
            logger.info("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
                logger.info("RequestId: "+rhmd.getRequestId());
                logger.info("Timestamp: "+rhmd.getTimestamp());
            }
            logger.info("Message: "+ex.getMessage());
            logger.info("StatusCode: "+ex.getStatusCode());
            logger.info("ErrorCode: "+ex.getErrorCode());
            logger.info("ErrorType: "+ex.getErrorType());
            throw ex;
        }
	}

//	private String postRequestToAms(String method, String HttpRequestUrl, String action, String version, TreeMap<String, String> requestParams) throws Exception {
//		//Calculate queryString
//		TreeMap<String, String> params = new TreeMap<>();
//		params.put("AWSAccessKeyId", awsAccessKey);
//		params.put("Action", action);
//		params.put("MarketplaceId", marketPlaceId);
//		params.put("SellerId", sellerId);
//		params.put("SignatureMethod", "HmacSHA256");
//		params.put("SignatureVersion", "2");
//		params.put("Timestamp", getCurrentTimestampInIso8601Format());
//		params.put("Version", version);
//		if (requestParams != null) {
//			params.putAll(requestParams);
//		}
//		String queryString = buildQueryString(params, (method.equals("GET")?true: false));
//		
//		//Append signature to body
//		String signature =  buildSignature(method + "\n" +
//				serviceURL + "\n" +
//				  HttpRequestUrl + "\n" +
//				  buildQueryString(params, true));
//		queryString = queryString.concat(String.format("&Signature=%s", URLEncoder.encode(signature, "UTF-8")));
//		
//		//Send request
//		String url = "https://".concat(serviceURL.concat(HttpRequestUrl));
//		logger.info("Sending " + method + " request with query-string " + queryString + " and url " + url);
//		if (method.equals("GET")) {
//			url = url.concat("?").concat(queryString);
//		}
//		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//		if (method.equals("POST")) {
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		}
//		conn.setRequestProperty("Accept", "text/xml");
//		conn.setRequestProperty("X-Amazon-User-Agent", "GoodlifeEcommerce/build0.1 (Language=Java; Host=localhost)");
//		conn.setRequestMethod(method);
//		conn.setDoOutput(true);
//
//		//Send body in case of post
//		if (method.equals("POST")) {
//			// Send message content.
//			logger.info("Adding body");
//			OutputStream os = conn.getOutputStream();
//			os.write(queryString.getBytes());
//			os.flush();
//			os.close();
//		}
//			
//		// Read response.
//		logger.info("response_code for " + url + " is ::" + conn.getResponseCode());
//		if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
//			InputStream inputStream = conn.getInputStream();
//			final int bufferSize = 1024;
//			final char[] buffer = new char[bufferSize];
//			final StringBuilder out = new StringBuilder();
//			Reader in = new InputStreamReader(inputStream, "UTF-8");
//			for (;;) {
//				int rsz = in.read(buffer, 0, buffer.length);
//				if (rsz < 0)
//					break;
//				out.append(buffer, 0, rsz);
//			}
//			logger.info("response_body for " + url + " is ::" + out.toString());
//			return out.toString();
//		} else if (conn.getResponseCode() == 204) {
//			return null;
//		} else if (conn.getResponseCode() == 401) {
//			throw new UnauthorizedException("User is not authorized");
//		} else if (conn.getResponseCode() == 404) {
//			throw new NotFoundException("No results found");
//		} else if (conn.getResponseCode() == 403) {
//			throw new ForbiddenException("User is forbidden");
//		} else {
//			InputStream inputStream = conn.getErrorStream();
//			final int bufferSize = 1024;
//			final char[] buffer = new char[bufferSize];
//			final StringBuilder out = new StringBuilder();
//			Reader in = new InputStreamReader(inputStream, "UTF-8");
//			for (;;) {
//				int rsz = in.read(buffer, 0, buffer.length);
//				if (rsz < 0)
//					break;
//				out.append(buffer, 0, rsz);
//			}
//			logger.debug("response_body for " + url + " is ::" + out.toString());
//			if (conn.getResponseCode() == 400) {
//				throw new BadRequestException(
//						"Got invalid response from MWS " + conn.getResponseCode() + " and error " + out.toString());
//			}
//			else {
//				throw new InternalException(
//					"Got invalid response from MWS " + conn.getResponseCode() + " and error " + out.toString());
//			}
//		}
//	}
//	
//	private String buildQueryString(TreeMap<String, String> params, Boolean urlEncode) throws UnsupportedEncodingException {
//		StringBuilder sb = new StringBuilder();
//		for (String paramName: params.keySet()) {
//			if (urlEncode) {
//				sb.append(URLEncoder.encode(paramName, "UTF-8"));
//				sb.append("=");
//				sb.append(URLEncoder.encode(params.get(paramName), "UTF-8"));
//				sb.append("&");
//			}
//			else {
//				sb.append(paramName);
//				sb.append("=");
//				sb.append(params.get(paramName));
//				sb.append("&");
//			}
//		}
//		return sb.substring(0, sb.length() - 1); //remove & in end
//	}
//
//	
//	private String buildSignature(String message) throws InvalidKeyException, NoSuchAlgorithmException {	
//		String signature = CryptoService.hamcSha256(mwsSecretKey, message);
//		logger.info("signature for message " + message + " secret-key " + mwsSecretKey + " is " + signature);
//		return signature;
//	}
//	
//	private String getCurrentTimestampInIso8601Format() {
//		TimeZone tz = TimeZone.getTimeZone("UTC");
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//		df.setTimeZone(tz);
//		return df.format(new Date());
//	}

	 /**
     * Get an async client connection ready to use.
     *
     * @return A ready to use client connection.
     */
    public static synchronized MarketplaceWebServiceProductsAsyncClient getAsyncClient(String serviceURL, String awsAccessKey, String mwsSecretKey) {
        if (client==null) {
            MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
            config.setServiceURL(serviceURL);
            // Set other client connection configurations here.
            client = new MarketplaceWebServiceProductsAsyncClient(awsAccessKey, mwsSecretKey, 
                    "seller-api", "0.1", config, null);
        }
        return client;
    }
	
}
