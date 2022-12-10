package com.flab.fbayshop.common.dto.response;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;

import com.flab.fbayshop.error.dto.ErrorType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {

    private int code;

    private String message;

    public static ResponseEntity<ErrorResponse> error(ErrorType errorType) {
        return error(errorType, errorType.getMessage());
    }

    public static ResponseEntity<ErrorResponse> error(ErrorType errorType, String message) {
        return ResponseEntity.status(errorType.getStatus())
            .body(new ErrorResponse(errorType.getCode(), message));
    }

}
