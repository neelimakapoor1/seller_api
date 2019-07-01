package com.goodlife.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class RequestInterceptor extends HandlerInterceptorAdapter {
	//private Logger logger = Logger.getLogger(RequestInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
			throws Exception {
		if (!req.getMethod().equals("OPTIONS")) {
			//Validate api-key
			String apiKey = req.getHeader("API-KEY");
			validateApiKeyAndReturnUserId(apiKey);
		}
		return true;
	}
	
	private String validateApiKeyAndReturnUserId(String apiKey) {
		return null;
	}
}