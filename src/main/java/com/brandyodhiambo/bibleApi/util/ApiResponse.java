package com.brandyodhiambo.bibleApi.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@JsonPropertyOrder({
		"success",
		"message"
})
public class ApiResponse implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 7702134516418120340L;

	@JsonProperty("success")
	private Boolean success;

	@JsonProperty("message")
	private String message;

	@JsonIgnore
	private HttpStatus status;

	public ApiResponse() {

	}

	public ApiResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse(Boolean success, String message, HttpStatus httpStatus) {
		this.success = success;
		this.message = message;
		this.status = httpStatus;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
