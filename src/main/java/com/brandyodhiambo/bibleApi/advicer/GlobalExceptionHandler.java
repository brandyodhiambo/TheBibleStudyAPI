package com.brandyodhiambo.bibleApi.advicer;

import com.brandyodhiambo.bibleApi.exception.*;
import com.brandyodhiambo.bibleApi.util.ApiResponse;
import com.brandyodhiambo.bibleApi.util.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BibleApiException.class)
	public ResponseEntity<ApiResponse> handleBibleApiException(BibleApiException exception) {
		ApiResponse apiResponse = new ApiResponse(false, exception.getMessage());
		return new ResponseEntity<>(apiResponse, exception.getStatus());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
		logger.warn("Unauthorized Access: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getApiResponse(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException exception) {
		return new ResponseEntity<>(exception.getApiResponse(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
		return new ResponseEntity<>(exception.getApiResponse(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
		logger.warn("Access Denied: {}", ex.getMessage());
		ApiResponse apiResponse = new ApiResponse(false, "You do not have permission to access this resource.");
		return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
		logger.warn("Validation Error: {}", ex.getMessage());
		List<String> messages = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + " - " + error.getDefaultMessage())
				.toList();
		return new ResponseEntity<>(new ExceptionResponse(messages, "Validation Error", 400), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '"
				+ Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";
		return new ResponseEntity<>(new ExceptionResponse(List.of(message), "Bad Request", 400), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ExceptionResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		logger.warn("Method Not Allowed: {}", ex.getMessage());
		String message = "Request method '" + ex.getMethod() + "' is not supported. Supported methods: " + ex.getSupportedHttpMethods();
		return new ResponseEntity<>(new ExceptionResponse(List.of(message), "Method Not Allowed", 405), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
		return new ResponseEntity<>(new ExceptionResponse(List.of("Please provide Request Body in valid JSON format"), "Bad Request", 400), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
		logger.error("Unhandled Exception: {}", ex.getMessage(), ex);
		ApiResponse apiResponse = new ApiResponse(false, "An unexpected error occurred: " + ex.getMessage());
		return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
