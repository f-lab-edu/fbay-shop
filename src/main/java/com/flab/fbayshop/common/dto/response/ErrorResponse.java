package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flab.fbayshop.error.dto.ErrorType;

import lombok.Getter;

@Getter
public class ErrorResponse implements Serializable {

    private final int code;

    private final String message;

    @JsonCreator
    private ErrorResponse(@JsonProperty("code") int code, @JsonProperty("messages") String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> error(ErrorType errorType) {
        return error(errorType, errorType.getMessage());
    }

    public static ResponseEntity<ErrorResponse> error(ErrorType errorType, String message) {
        return ResponseEntity.status(errorType.getStatus())
            .body(new ErrorResponse(errorType.getCode(), message));
    }

}
