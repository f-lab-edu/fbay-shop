package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class ApiResponse<T> implements Serializable {

	private final int code;

	private final String message;

	private final T data;

	private ApiResponse(int code, T data, String message) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> ResponseEntity<ApiResponse> ok(T data) {
		return ApiResponse.ok(data, "");
	}

	public static <T> ResponseEntity<ApiResponse> ok(T data, String message) {
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, message));
	}

	public static <T> ResponseEntity<ApiResponse> created(T data) {
		return ApiResponse.created(data, "", "");
	}

	public static <T> ResponseEntity<ApiResponse> created(T data, String message) {
		return ApiResponse.created(data, message, "");
	}

	public static <T> ResponseEntity<ApiResponse> created(T data, String message, String uriStr) {
		return ResponseEntity.created(URI.create(uriStr))
			.body(new ApiResponse(HttpStatus.CREATED.value(), data, message));
	}

}

