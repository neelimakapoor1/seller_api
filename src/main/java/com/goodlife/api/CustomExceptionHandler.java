package com.goodlife.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.postgresql.util.PSQLException;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.amazonaws.mws.products.MarketplaceWebServiceProductsException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.goodlife.exception.BadRequestException;
import com.goodlife.exception.ForbiddenException;
import com.goodlife.exception.InternalException;
import com.goodlife.exception.NotFoundException;
import com.goodlife.exception.NotImplementedException;
import com.goodlife.exception.UnauthorizedException;
import com.goodlife.model.ErrorResponse;;

@ControllerAdvice
public class CustomExceptionHandler  {
	private Logger logger = Logger.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler({ MissingServletRequestParameterException.class })
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, HttpHeaders headers, 
			HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		String error = ex.getParameterName() + " parameter is missing";

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(error), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ServletRequestBindingException.class })
	public ResponseEntity<ErrorResponse> handleServletRequestBindingException(
			ServletRequestBindingException ex, HttpHeaders headers,   HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		List<String> errors = new ArrayList<String>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + 
					violation.getPropertyPath() + ": " + violation.getMessage());
		}

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(errors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(error), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, 
			HttpHeaders headers, 
			HttpStatus status, 
			WebRequest request) {
		logger.error(ex.getMessage(), ex);
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		//ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(builder.toString()), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, 
			HttpHeaders headers, 
			HttpStatus status, 
			WebRequest request) {
		logger.error(ex.getMessage(), ex);
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		//ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(builder.substring(0, builder.length() - 2)), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public final ResponseEntity<ErrorResponse> handleBadRequestResponseStatus(MethodArgumentNotValidException ex) {
		logger.error(ex.getMessage(), ex);
		List<String> errors = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message(errors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message("Resource Not Found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<ErrorResponse>(new ErrorResponse().message("Resource Not Found"), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NoResultException.class)
	public final ResponseEntity<ErrorResponse> handleNoResultException(NoResultException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message("No results found");
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public final ResponseEntity<ErrorResponse> handleUnauthorisedException(UnauthorizedException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(InternalException.class)
	public final ResponseEntity<ErrorResponse> handleUnauthorisedException(InternalException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ForbiddenException.class)
	public final ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	
	@ExceptionHandler(MarketplaceWebServiceProductsException.class)
	public final ResponseEntity<ErrorResponse> handleMarketplaceWebServiceProductsException(MarketplaceWebServiceProductsException ex) {
		ErrorResponse errorResponse = new ErrorResponse().message(ex.getMessage());
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
		Throwable rootCause = com.google.common.base.Throwables.getRootCause(ex);
		if (ex instanceof EntityNotFoundException) {
			ErrorResponse errorResponse = new ErrorResponse().message("No results found");
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		}
		else if (rootCause instanceof PSQLException) {
			PSQLException psqlEx = (PSQLException) rootCause;
			if ("22023".equals(psqlEx.getSQLState())) {
				ErrorResponse errorResponse = new ErrorResponse().message(psqlEx.getServerErrorMessage().getMessage());
				logger.error(ex.getMessage(), ex);
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
			//Handle duplicate-constraint violation
			if ("23505".equals(psqlEx.getSQLState())) {
				String message = "Duplicate value found";
				if (psqlEx.getServerErrorMessage().getMessage().contains("_label_idx")) {
					message += " for label";
				}
				else if (psqlEx.getServerErrorMessage().getMessage().contains("_name_idx")) {
					message += " for name";
				}
				ErrorResponse errorResponse = new ErrorResponse().message(message);
				logger.error(ex.getMessage(), ex);
				return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
			}
			//Handle null-constraint violation
			if ("23502".equals(psqlEx.getSQLState())) {
				ErrorResponse errorResponse = new ErrorResponse().message(psqlEx.getServerErrorMessage().getMessage());
				logger.error(ex.getMessage(), ex);
				return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
			}
			//Handle invalid JSON
			if ("22P02".equals(psqlEx.getSQLState())) {
				ErrorResponse errorResponse = new ErrorResponse().message(psqlEx.getServerErrorMessage().getMessage());
				logger.error(ex.getMessage(), ex);
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
		}
		else if (rootCause instanceof JSONException) {
			JSONException jsonEx = (JSONException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		else if (rootCause instanceof JsonParseException) {
			JsonParseException jsonEx = (JsonParseException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		else if (rootCause instanceof JsonMappingException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		else if (rootCause instanceof UnauthorizedException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
		else if (rootCause instanceof InternalException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if (rootCause instanceof ForbiddenException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
		}
		else if (rootCause instanceof BadRequestException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		else if (rootCause instanceof NotFoundException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		}
		else if (rootCause instanceof NotImplementedException) {
			JsonMappingException jsonEx = (JsonMappingException) rootCause;
			ErrorResponse errorResponse = new ErrorResponse().message(jsonEx.getMessage());
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_IMPLEMENTED);
		}

		ErrorResponse errorResponse = new ErrorResponse().message("Internal Server Error");
		logger.error(ex.getMessage(), ex);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
