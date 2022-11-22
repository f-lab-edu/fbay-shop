package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;

import com.flab.fbayshop.error.dto.ErrorType;

import lombok.Getter;

@Getter
public class ErrorResponse implements Serializable {

    private final int code;

    private final String message;

    private ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> error(ErrorType errorType) {
        return ResponseEntity.status(errorType.getStatus())
            .body(new ErrorResponse(errorType.getCode(), errorType.getMessage()));
    }

}
