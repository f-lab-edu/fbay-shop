package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

	private int code;

	private T data;

	private String message;


	public static <T> ResponseEntity<ApiResponse> ok(T data) {
		return ApiResponse.ok(data, "");
	}

	public static <T> ResponseEntity<ApiResponse> ok(T data, String message) {
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), data, message));
	}

	public static <T> ResponseEntity<ApiResponse> created(T data) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), data, ""));
	}

}

