package com.goodlife.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.goodlife.exception.BadRequestException;
import com.goodlife.exception.InternalException;
import com.goodlife.exception.NotFoundException;
import com.goodlife.exception.UnauthorizedException;

@Service
public class ParseHubService {
	Logger logger = Logger.getLogger(ParseHubService.class);

	@Value("${parsehub.api.key}")
	private String apiKey;

	@Value("${parsehub.project.token}")
	private String projectToken;
	
	public Map<String, Integer> getProductRankForKeywords(List<String> keywords, String asin) throws Exception {
		//Run project for keyword-search
		JSONObject startValue = new JSONObject();
		startValue.put("keywords", new JSONArray(keywords));
		TreeMap<String, String> params = new TreeMap<>();
		params.put("start_value_override", startValue.toString());
		//params.put("start_url", "https://www.amazon.in/");
		String response = sendRequestToParseHub("POST", "/projects/".concat(projectToken).concat("/run"), params, false);
		JSONObject jsonResponse = new JSONObject(response);
		String runToken = jsonResponse.getString("run_token");
		logger.info("response for run " + response);
		
		//Build map between keyword and rank
		Map<String, Integer> keywordRankMap = new HashMap<>();
		
		//Fetch the keyword-search results
		logger.info("checking if data is ready after 120 seconds for run token " + runToken);
		Thread.sleep(1000 * 120); //10 seconds for each Next button click
		response = sendRequestToParseHub("GET", "/runs/".concat(runToken), null, false);
		jsonResponse = new JSONObject(response);
		logger.info("response for run " + response);
		while(jsonResponse.getInt("data_ready") == 0) {
			//Wait for 12 seconds and retry
			logger.info("data not ready, retrying after 12 seconds");
			Thread.sleep(1000 * 12); //max 25 calls in 5 mins
			response = sendRequestToParseHub("GET", "/runs/".concat(runToken), null, false);
			jsonResponse = new JSONObject(response);
			logger.info("response for run " + response);
		}
		
		logger.info("data ready, fetching data for run " + runToken);
		response = sendRequestToParseHub("GET", "/runs/".concat(runToken).concat("/data"), null, true);
		jsonResponse = new JSONObject(response);
		JSONArray keywordResponseList = jsonResponse.getJSONArray("keywordlist");
		for (int i = 0; i < keywordResponseList.length(); i++) {
			String keyword = keywords.get(i);
			JSONArray productDataList = keywordResponseList.getJSONObject(i).optJSONArray("Product");
			if (productDataList != null) {
				logger.debug("products found for keyword " + keyword + " are " + productDataList.length());
				Integer paidRankCounter = 0;
				Integer organicRankCounter = 0;
				for (int j = 0; j < productDataList.length(); j++) {
					//Get product with matching asin
					String productUrl = productDataList.getJSONObject(j).getString("url");
					if (productUrl.indexOf("adId=") > -1 && productUrl.indexOf("%2Fdp%2F") > -1) {
						paidRankCounter++;
						if (productUrl.indexOf("%2Fdp%2F" + asin + "%2Fref%3D") > -1) {
							//Paid rank
							logger.info("asin found with paid rank");
							keywordRankMap.put(keyword + "_paid", paidRankCounter);
						}
					}
					else if (productUrl.indexOf("/dp/") > -1) {
						organicRankCounter++;
						if (productUrl.indexOf("/dp/" + asin + "/ref=") > -1) {
							//Organic rank
							logger.info("asin found with organic rank");
							keywordRankMap.put(keyword + "_organic", organicRankCounter);
						}
					}
					if (keywordRankMap.containsKey(keyword + "_paid") && keywordRankMap.containsKey(keyword + "_organic")) {
						break;
					}
				}
			}
			if (!keywordRankMap.containsKey(keyword + "_paid")) {
				logger.info("asin not found in sponsored products");
				keywordRankMap.put(keyword + "_paid", 0);
			}
			if (!keywordRankMap.containsKey(keyword + "_organic")) {
				logger.info("asin not found in organic products");
				keywordRankMap.put(keyword + "_organic", 0);
			}
		}
		return keywordRankMap;
	}
	
	private String sendRequestToParseHub(String method, String httpRequestUrl, TreeMap<String, String> requestParams, boolean hasGzipResponse) throws Exception {
		//Build url
		String url = "https://www.parsehub.com/api/v2/".concat(httpRequestUrl);
		
		//Build query-string
		TreeMap<String, String> params = new TreeMap<>();
		params.put("api_key", apiKey);
		if (requestParams != null) {
			params.putAll(requestParams);
		}
		String queryString = buildQueryString(params, (method.equals("GET")?true: false));
		logger.info("Sending " + method + " request to url " + url + " with params " + queryString);
		if (method.equals("GET")) {
			url = url.concat("?").concat(queryString);
		}
		
		//Send request
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (method.equals("POST")) {
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		}
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-ParseHub-User-Agent", "GoodlifeEcommerce/build0.1 (Language=Java; Host=localhost)");
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
			if (hasGzipResponse) {
				inputStream = new GZIPInputStream(conn.getInputStream());
			}
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
			return out.toString();
		} else if (conn.getResponseCode() == 204) {
			return null;
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
			logger.info("response_body for " + url + " is ::" + out.toString());
			if (conn.getResponseCode() == 401) {
				throw new UnauthorizedException("User is not authorized");
			} 
			else if (conn.getResponseCode() == 400) {
				throw new BadRequestException(
						"Got invalid response from ParseHub " + conn.getResponseCode() + " and error " + out.toString());
			}
			else {
				throw new InternalException(
						"Got invalid response from ParseHub " + conn.getResponseCode() + " and error " + out.toString());
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
}
