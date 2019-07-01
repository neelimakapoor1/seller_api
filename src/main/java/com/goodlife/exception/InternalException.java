package com.goodlife.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6155575244805468088L;

	public InternalException(String exception) {
		super(exception);
	}
}
