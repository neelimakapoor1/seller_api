package com.goodlife.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class CryptoService {
	
	private static Logger logger = Logger.getLogger(CryptoService.class);
	
	
	public static String hamcSha256(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException
	{
		logger.debug("hmacSha250 in: " + key + " " + message);
		Mac sha256HMAC = Mac.getInstance("HmacSHA256");
	     SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
	     sha256HMAC.init(secretKey);
	     String hash = Base64.encodeBase64String(sha256HMAC.doFinal(message.getBytes()));
	     logger.debug("hmacSha250 out: " + hash);
	     return  hash;
	}
}
